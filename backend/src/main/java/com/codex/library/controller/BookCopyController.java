package com.codex.library.controller;

import com.codex.library.common.ApiResponse;
import com.codex.library.common.PageResult;
import com.codex.library.dto.bookcopy.BookCopySaveRequest;
import com.codex.library.dto.bookcopy.BookCopyView;
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
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookService bookService;

    public BookCopyController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ApiResponse<PageResult<BookCopyView>> page(@RequestParam(defaultValue = "1") int current,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long bookId,
                                                      @RequestParam(required = false) String status) {
        return ApiResponse.success(bookService.pageCopies(current, size, keyword, bookId, status));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<Void> create(@Valid @RequestBody BookCopySaveRequest request) {
        bookService.createCopy(request);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody BookCopySaveRequest request) {
        bookService.updateCopy(id, request);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bookService.deleteCopy(id);
        return ApiResponse.success("删除成功", null);
    }
}
