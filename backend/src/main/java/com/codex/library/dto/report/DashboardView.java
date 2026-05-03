package com.codex.library.dto.report;

public record DashboardView(
        long totalBooks,
        long totalCopies,
        long availableCopies,
        long borrowedCopies,
        long overdueCount,
        long totalReaders
) {
}
