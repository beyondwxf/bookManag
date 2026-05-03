package com.codex.library.controller;

import com.codex.library.common.ApiResponse;
import com.codex.library.common.PageResult;
import com.codex.library.dto.loan.BorrowRequest;
import com.codex.library.dto.loan.LoanView;
import com.codex.library.dto.loan.ReturnRequest;
import com.codex.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<Void> borrow(@Valid @RequestBody BorrowRequest request) {
        loanService.borrow(request);
        return ApiResponse.success("借阅成功", null);
    }

    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        loanService.returnBook(request);
        return ApiResponse.success("归还成功", null);
    }

    @GetMapping("/current")
    public ApiResponse<PageResult<LoanView>> current(@RequestParam(defaultValue = "1") int current,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) Long readerId) {
        return ApiResponse.success(loanService.currentLoans(current, size, readerId));
    }

    @GetMapping("/history")
    public ApiResponse<PageResult<LoanView>> history(@RequestParam(defaultValue = "1") int current,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) Long readerId) {
        return ApiResponse.success(loanService.historyLoans(current, size, readerId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<PageResult<LoanView>> overdue(@RequestParam(defaultValue = "1") int current,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) Long readerId) {
        return ApiResponse.success(loanService.overdueLoans(current, size, readerId));
    }
}
