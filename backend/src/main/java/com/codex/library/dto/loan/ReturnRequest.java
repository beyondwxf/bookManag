package com.codex.library.dto.loan;

import jakarta.validation.constraints.NotNull;

public record ReturnRequest(@NotNull(message = "借阅记录不能为空") Long loanId) {
}
