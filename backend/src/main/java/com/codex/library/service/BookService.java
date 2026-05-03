package com.codex.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codex.library.common.PageResult;
import com.codex.library.dto.book.BookSaveRequest;
import com.codex.library.dto.book.BookView;
import com.codex.library.dto.bookcopy.BookCopySaveRequest;
import com.codex.library.dto.bookcopy.BookCopyView;
import com.codex.library.entity.Book;
import com.codex.library.entity.BookCategory;
import com.codex.library.entity.BookCopy;
import com.codex.library.entity.LoanRecord;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.BookCategoryMapper;
import com.codex.library.mapper.BookCopyMapper;
import com.codex.library.mapper.BookMapper;
import com.codex.library.mapper.LoanRecordMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BookService {

    private final BookMapper bookMapper;
    private final BookCategoryMapper categoryMapper;
    private final BookCopyMapper bookCopyMapper;
    private final LoanRecordMapper loanRecordMapper;

    public BookService(BookMapper bookMapper,
                       BookCategoryMapper categoryMapper,
                       BookCopyMapper bookCopyMapper,
                       LoanRecordMapper loanRecordMapper) {
        this.bookMapper = bookMapper;
        this.categoryMapper = categoryMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.loanRecordMapper = loanRecordMapper;
    }

    public PageResult<BookView> pageBooks(int current, int size, String keyword, Long categoryId) {
        Page<Book> page = new Page<>(current, size);
        IPage<Book> result = bookMapper.selectPage(page, Wrappers.<Book>lambdaQuery()
                .and(StringUtils.hasText(keyword), wrapper -> wrapper.like(Book::getTitle, keyword)
                        .or()
                        .like(Book::getAuthor, keyword)
                        .or()
                        .like(Book::getIsbn, keyword))
                .eq(categoryId != null, Book::getCategoryId, categoryId)
                .orderByDesc(Book::getCreatedAt));
        Map<Long, BookCategory> categoryMap = categoryMapper.selectList(Wrappers.<BookCategory>lambdaQuery()).stream()
                .collect(Collectors.toMap(BookCategory::getId, Function.identity()));
        List<Long> bookIds = result.getRecords().stream().map(Book::getId).toList();
        Map<Long, List<BookCopy>> copyMap = bookIds.isEmpty() ? Map.of() : bookCopyMapper.selectList(Wrappers.<BookCopy>lambdaQuery()
                        .in(BookCopy::getBookId, bookIds))
                .stream()
                .collect(Collectors.groupingBy(BookCopy::getBookId));
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords().stream()
                .map(book -> {
                    List<BookCopy> copies = copyMap.getOrDefault(book.getId(), List.of());
                    long availableCopies = copies.stream().filter(copy -> "AVAILABLE".equals(copy.getStatus())).count();
                    BookCategory category = categoryMap.get(book.getCategoryId());
                    return new BookView(
                            book.getId(),
                            book.getCategoryId(),
                            category == null ? null : category.getName(),
                            book.getIsbn(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getPublisher(),
                            book.getPublishDate(),
                            book.getDescription(),
                            copies.size(),
                            availableCopies
                    );
                }).toList());
    }

    public BookView getBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        BookCategory category = categoryMapper.selectById(book.getCategoryId());
        List<BookCopy> copies = bookCopyMapper.selectList(Wrappers.<BookCopy>lambdaQuery().eq(BookCopy::getBookId, id));
        long availableCopies = copies.stream().filter(copy -> "AVAILABLE".equals(copy.getStatus())).count();
        return new BookView(book.getId(), book.getCategoryId(), category == null ? null : category.getName(), book.getIsbn(),
                book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(), book.getDescription(), copies.size(), availableCopies);
    }

    @Transactional
    public void createBook(BookSaveRequest request) {
        if (categoryMapper.selectById(request.categoryId()) == null) {
            throw new BusinessException("图书分类不存在");
        }
        long duplicate = bookMapper.selectCount(Wrappers.<Book>lambdaQuery().eq(Book::getIsbn, request.isbn()));
        if (duplicate > 0) {
            throw new BusinessException("ISBN 已存在");
        }
        Book book = new Book();
        book.setCategoryId(request.categoryId());
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublisher(request.publisher());
        book.setPublishDate(request.publishDate());
        book.setDescription(request.description());
        book.setDeleted(0);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        bookMapper.insert(book);
    }

    @Transactional
    public void updateBook(Long id, BookSaveRequest request) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        long duplicate = bookMapper.selectCount(Wrappers.<Book>lambdaQuery()
                .eq(Book::getIsbn, request.isbn())
                .ne(Book::getId, id));
        if (duplicate > 0) {
            throw new BusinessException("ISBN 已存在");
        }
        book.setCategoryId(request.categoryId());
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublisher(request.publisher());
        book.setPublishDate(request.publishDate());
        book.setDescription(request.description());
        book.setUpdatedAt(LocalDateTime.now());
        bookMapper.updateById(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (bookMapper.selectById(id) == null) {
            throw new BusinessException("图书不存在");
        }
        long copies = bookCopyMapper.selectCount(Wrappers.<BookCopy>lambdaQuery().eq(BookCopy::getBookId, id));
        if (copies > 0) {
            throw new BusinessException("图书存在副本，不能删除");
        }
        bookMapper.deleteById(id);
    }

    public PageResult<BookCopyView> pageCopies(int current, int size, String keyword, Long bookId, String status) {
        Page<BookCopy> page = new Page<>(current, size);
        IPage<BookCopy> result = bookCopyMapper.selectPage(page, Wrappers.<BookCopy>lambdaQuery()
                .like(StringUtils.hasText(keyword), BookCopy::getBarcode, keyword)
                .eq(bookId != null, BookCopy::getBookId, bookId)
                .eq(StringUtils.hasText(status), BookCopy::getStatus, status)
                .orderByDesc(BookCopy::getCreatedAt));
        List<Long> bookIds = result.getRecords().stream().map(BookCopy::getBookId).toList();
        Map<Long, Book> bookMap = bookIds.isEmpty() ? Map.of() : bookMapper.selectBatchIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords().stream()
                .map(copy -> {
                    Book book = bookMap.get(copy.getBookId());
                    return new BookCopyView(copy.getId(), copy.getBookId(), copy.getBarcode(), copy.getLocationCode(), copy.getStatus(),
                            copy.getVersion(), book == null ? null : book.getTitle(), book == null ? null : book.getIsbn(), book == null ? null : book.getAuthor());
                }).toList());
    }

    @Transactional
    public void createCopy(BookCopySaveRequest request) {
        if (bookMapper.selectById(request.bookId()) == null) {
            throw new BusinessException("图书不存在");
        }
        long duplicate = bookCopyMapper.selectCount(Wrappers.<BookCopy>lambdaQuery().eq(BookCopy::getBarcode, request.barcode()));
        if (duplicate > 0) {
            throw new BusinessException("副本条码已存在");
        }
        BookCopy copy = new BookCopy();
        copy.setBookId(request.bookId());
        copy.setBarcode(request.barcode());
        copy.setLocationCode(request.locationCode());
        copy.setStatus(StringUtils.hasText(request.status()) ? request.status() : "AVAILABLE");
        copy.setVersion(0);
        copy.setDeleted(0);
        copy.setCreatedAt(LocalDateTime.now());
        copy.setUpdatedAt(LocalDateTime.now());
        bookCopyMapper.insert(copy);
    }

    @Transactional
    public void updateCopy(Long id, BookCopySaveRequest request) {
        BookCopy copy = bookCopyMapper.selectById(id);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        long duplicate = bookCopyMapper.selectCount(Wrappers.<BookCopy>lambdaQuery()
                .eq(BookCopy::getBarcode, request.barcode())
                .ne(BookCopy::getId, id));
        if (duplicate > 0) {
            throw new BusinessException("副本条码已存在");
        }
        copy.setBookId(request.bookId());
        copy.setBarcode(request.barcode());
        copy.setLocationCode(request.locationCode());
        if (StringUtils.hasText(request.status())) {
            copy.setStatus(request.status());
        }
        copy.setUpdatedAt(LocalDateTime.now());
        bookCopyMapper.updateById(copy);
    }

    @Transactional
    public void deleteCopy(Long id) {
        BookCopy copy = bookCopyMapper.selectById(id);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        long currentLoans = loanRecordMapper.selectCount(Wrappers.<LoanRecord>lambdaQuery()
                .eq(LoanRecord::getBookCopyId, id)
                .in(LoanRecord::getStatus, List.of("BORROWED", "OVERDUE")));
        if (currentLoans > 0) {
            throw new BusinessException("副本仍在借阅中，不能删除");
        }
        bookCopyMapper.deleteById(id);
    }

    public BookCopy getCopy(Long id) {
        return bookCopyMapper.selectById(id);
    }
}
