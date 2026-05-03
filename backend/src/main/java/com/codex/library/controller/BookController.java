package com.codex.library.controller;

import com.codex.library.common.ApiResponse;
import com.codex.library.common.PageResult;
import com.codex.library.dto.book.BookSaveRequest;
import com.codex.library.dto.book.BookView;
import com.codex.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ApiResponse<PageResult<BookView>> page(@RequestParam(defaultValue = "1") int current,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(bookService.pageBooks(current, size, keyword, categoryId));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookView> detail(@PathVariable Long id) {
        return ApiResponse.success(bookService.getBook(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> create(@Valid @RequestBody BookSaveRequest request) {
        bookService.createBook(request);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody BookSaveRequest request) {
        bookService.updateBook(id, request);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.success("删除成功", null);
    }
}
