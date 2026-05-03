package com.codex.library.controller;

import com.codex.library.common.ApiResponse;
import com.codex.library.dto.report.DashboardView;
import com.codex.library.dto.report.LoanTrendView;
import com.codex.library.service.ReportService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<DashboardView> dashboard() {
        return ApiResponse.success(reportService.dashboard());
    }

    @GetMapping("/loans")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResponse<List<LoanTrendView>> loans() {
        return ApiResponse.success(reportService.loanTrend());
    }
}
