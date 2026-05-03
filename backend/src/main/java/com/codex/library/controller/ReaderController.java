package com.codex.library.controller;

import com.codex.library.common.ApiResponse;
import com.codex.library.common.PageResult;
import com.codex.library.dto.reader.ReaderSaveRequest;
import com.codex.library.dto.reader.ReaderView;
import com.codex.library.service.ReaderService;
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
@RequestMapping("/api/readers")
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public ApiResponse<PageResult<ReaderView>> page(@RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String status) {
        return ApiResponse.success(readerService.page(current, size, keyword, status));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<Void> create(@Valid @RequestBody ReaderSaveRequest request) {
        readerService.create(request);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ReaderSaveRequest request) {
        readerService.update(id, request);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        readerService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
