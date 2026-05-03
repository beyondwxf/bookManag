package com.codex.library.dto.loan;

import jakarta.validation.constraints.NotNull;

public record BorrowRequest(
        @NotNull(message = "读者不能为空")
        Long readerId,
        @NotNull(message = "副本不能为空")
        Long bookCopyId,
        Integer dueDays
) {
}
