package com.codex.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codex.library.common.PageResult;
import com.codex.library.dto.loan.BorrowRequest;
import com.codex.library.dto.loan.LoanView;
import com.codex.library.dto.loan.ReturnRequest;
import com.codex.library.entity.Book;
import com.codex.library.entity.BookCopy;
import com.codex.library.entity.LoanRecord;
import com.codex.library.entity.ReaderProfile;
import com.codex.library.entity.User;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.BookCopyMapper;
import com.codex.library.mapper.BookMapper;
import com.codex.library.mapper.LoanRecordMapper;
import com.codex.library.mapper.ReaderProfileMapper;
import com.codex.library.mapper.UserMapper;
import com.codex.library.security.LoginUser;
import com.codex.library.security.SecurityContextHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    private final LoanRecordMapper loanRecordMapper;
    private final BookCopyMapper bookCopyMapper;
    private final ReaderProfileMapper readerProfileMapper;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public LoanService(LoanRecordMapper loanRecordMapper,
                       BookCopyMapper bookCopyMapper,
                       ReaderProfileMapper readerProfileMapper,
                       UserMapper userMapper,
                       BookMapper bookMapper) {
        this.loanRecordMapper = loanRecordMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.readerProfileMapper = readerProfileMapper;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public void borrow(BorrowRequest request) {
        refreshOverdueStatuses();
        ReaderProfile reader = readerProfileMapper.selectById(request.readerId());
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (!"ACTIVE".equals(reader.getStatus())) {
            throw new BusinessException("读者账号已禁用");
        }
        long currentLoans = loanRecordMapper.selectCount(Wrappers.<LoanRecord>lambdaQuery()
                .eq(LoanRecord::getReaderId, request.readerId())
                .in(LoanRecord::getStatus, List.of("BORROWED", "OVERDUE")));
        if (currentLoans >= 5) {
            throw new BusinessException("读者当前借阅已达上限");
        }
        long overdueCount = loanRecordMapper.selectCount(Wrappers.<LoanRecord>lambdaQuery()
                .eq(LoanRecord::getReaderId, request.readerId())
                .eq(LoanRecord::getStatus, "OVERDUE"));
        if (overdueCount > 0) {
            throw new BusinessException("读者存在逾期未还记录，禁止借阅");
        }
        BookCopy copy = bookCopyMapper.selectById(request.bookCopyId());
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        int affected = bookCopyMapper.updateStatusIfMatch(copy.getId(), "AVAILABLE", "BORROWED");
        if (affected == 0) {
            throw new BusinessException("该图书副本当前不可借");
        }
        LoginUser currentUser = SecurityContextHelper.currentUser();
        LoanRecord record = new LoanRecord();
        record.setLoanNo(generateLoanNo());
        record.setReaderId(reader.getId());
        record.setBookCopyId(copy.getId());
        record.setBorrowedBy(currentUser.getUserId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueDate(LocalDate.now().plusDays(request.dueDays() == null ? 30 : request.dueDays()));
        record.setStatus("BORROWED");
        record.setDeleted(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        loanRecordMapper.insert(record);
    }

    @Transactional
    public void returnBook(ReturnRequest request) {
        refreshOverdueStatuses();
        LoanRecord record = loanRecordMapper.selectById(request.loanId());
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getReturnTime() != null || "RETURNED".equals(record.getStatus())) {
            throw new BusinessException("该借阅记录已归还");
        }
        int affected = bookCopyMapper.updateStatusIfMatch(record.getBookCopyId(), "BORROWED", "AVAILABLE");
        if (affected == 0) {
            throw new BusinessException("图书副本状态异常，无法归还");
        }
        LoginUser currentUser = SecurityContextHelper.currentUser();
        record.setReturnedBy(currentUser.getUserId());
        record.setReturnTime(LocalDateTime.now());
        record.setStatus("RETURNED");
        record.setUpdatedAt(LocalDateTime.now());
        loanRecordMapper.updateById(record);
    }

    public PageResult<LoanView> currentLoans(int current, int size, Long readerId) {
        refreshOverdueStatuses();
        return pageLoans(current, size, readerId, true, false);
    }

    public PageResult<LoanView> historyLoans(int current, int size, Long readerId) {
        refreshOverdueStatuses();
        return pageLoans(current, size, readerId, false, false);
    }

    public PageResult<LoanView> overdueLoans(int current, int size, Long readerId) {
        refreshOverdueStatuses();
        return pageLoans(current, size, readerId, false, true);
    }

    public void refreshOverdueStatuses() {
        loanRecordMapper.refreshOverdueStatus();
    }

    private PageResult<LoanView> pageLoans(int current, int size, Long readerId, boolean currentOnly, boolean overdueOnly) {
        LoginUser currentUser = SecurityContextHelper.currentUser();
        Long actualReaderId = resolveReaderId(currentUser, readerId);
        Page<LoanRecord> page = new Page<>(current, size);
        IPage<LoanRecord> result = loanRecordMapper.selectPage(page, Wrappers.<LoanRecord>lambdaQuery()
                .eq(actualReaderId != null, LoanRecord::getReaderId, actualReaderId)
                .in(currentOnly, LoanRecord::getStatus, List.of("BORROWED", "OVERDUE"))
                .eq(overdueOnly, LoanRecord::getStatus, "OVERDUE")
                .orderByDesc(LoanRecord::getBorrowTime));
        return buildLoanPage(result);
    }

    private PageResult<LoanView> buildLoanPage(IPage<LoanRecord> result) {
        List<Long> readerIds = result.getRecords().stream().map(LoanRecord::getReaderId).distinct().toList();
        List<Long> copyIds = result.getRecords().stream().map(LoanRecord::getBookCopyId).distinct().toList();
        List<Long> operatorIds = result.getRecords().stream()
                .flatMap(record -> java.util.stream.Stream.of(record.getBorrowedBy(), record.getReturnedBy()))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, ReaderProfile> readerMap = readerIds.isEmpty() ? Map.of() : readerProfileMapper.selectBatchIds(readerIds).stream()
                .collect(Collectors.toMap(ReaderProfile::getId, Function.identity()));
        Map<Long, BookCopy> copyMap = copyIds.isEmpty() ? Map.of() : bookCopyMapper.selectBatchIds(copyIds).stream()
                .collect(Collectors.toMap(BookCopy::getId, Function.identity()));
        Map<Long, Book> bookMap = copyMap.isEmpty() ? Map.of() : bookMapper.selectBatchIds(copyMap.values().stream().map(BookCopy::getBookId).distinct().toList()).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
        Map<Long, User> userMap = operatorIds.isEmpty() ? Map.of() : userMapper.selectBatchIds(operatorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords().stream()
                .map(record -> {
                    ReaderProfile reader = readerMap.get(record.getReaderId());
                    BookCopy copy = copyMap.get(record.getBookCopyId());
                    Book book = copy == null ? null : bookMap.get(copy.getBookId());
                    User borrowedBy = record.getBorrowedBy() == null ? null : userMap.get(record.getBorrowedBy());
                    User returnedBy = record.getReturnedBy() == null ? null : userMap.get(record.getReturnedBy());
                    return new LoanView(
                            record.getId(),
                            record.getLoanNo(),
                            record.getReaderId(),
                            reader == null ? null : reader.getReaderNo(),
                            reader == null ? null : reader.getName(),
                            book == null ? null : book.getTitle(),
                            copy == null ? null : copy.getBarcode(),
                            book == null ? null : book.getIsbn(),
                            record.getBorrowTime(),
                            record.getDueDate(),
                            record.getReturnTime(),
                            record.getStatus(),
                            borrowedBy == null ? null : borrowedBy.getDisplayName(),
                            returnedBy == null ? null : returnedBy.getDisplayName()
                    );
                }).toList());
    }

    private Long resolveReaderId(LoginUser currentUser, Long requestedReaderId) {
        if ("READER".equals(currentUser.getRoleCode())) {
            ReaderProfile profile = readerProfileMapper.findByUserId(currentUser.getUserId());
            if (profile == null) {
                throw new BusinessException("读者档案不存在");
            }
            return profile.getId();
        }
        return requestedReaderId;
    }

    private String generateLoanNo() {
        return "L" + System.currentTimeMillis();
    }
}
