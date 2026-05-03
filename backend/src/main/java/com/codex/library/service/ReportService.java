package com.codex.library.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codex.library.dto.report.DashboardView;
import com.codex.library.dto.report.LoanTrendView;
import com.codex.library.entity.Book;
import com.codex.library.entity.BookCopy;
import com.codex.library.entity.LoanRecord;
import com.codex.library.entity.ReaderProfile;
import com.codex.library.mapper.BookCopyMapper;
import com.codex.library.mapper.BookMapper;
import com.codex.library.mapper.LoanRecordMapper;
import com.codex.library.mapper.ReaderProfileMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;
    private final LoanRecordMapper loanRecordMapper;
    private final ReaderProfileMapper readerProfileMapper;
    private final LoanService loanService;

    public ReportService(BookMapper bookMapper,
                         BookCopyMapper bookCopyMapper,
                         LoanRecordMapper loanRecordMapper,
                         ReaderProfileMapper readerProfileMapper,
                         LoanService loanService) {
        this.bookMapper = bookMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.loanRecordMapper = loanRecordMapper;
        this.readerProfileMapper = readerProfileMapper;
        this.loanService = loanService;
    }

    public DashboardView dashboard() {
        loanService.refreshOverdueStatuses();
        return new DashboardView(
                bookMapper.selectCount(Wrappers.<Book>lambdaQuery()),
                bookCopyMapper.selectCount(Wrappers.<BookCopy>lambdaQuery()),
                bookCopyMapper.selectCount(Wrappers.<BookCopy>lambdaQuery().eq(BookCopy::getStatus, "AVAILABLE")),
                bookCopyMapper.selectCount(Wrappers.<BookCopy>lambdaQuery().eq(BookCopy::getStatus, "BORROWED")),
                loanRecordMapper.selectCount(Wrappers.<LoanRecord>lambdaQuery().eq(LoanRecord::getStatus, "OVERDUE")),
                readerProfileMapper.selectCount(Wrappers.<ReaderProfile>lambdaQuery())
        );
    }

    public List<LoanTrendView> loanTrend() {
        List<Map<String, Object>> rows = loanRecordMapper.loanTrend();
        return rows.stream()
                .map(row -> new LoanTrendView(String.valueOf(row.get("borrowDate")), ((Number) row.get("borrowCount")).longValue()))
                .toList();
    }
}
