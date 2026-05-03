package com.codex.library.dto.bookcopy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookCopySaveRequest(
        @NotNull(message = "图书不能为空")
        Long bookId,
        @NotBlank(message = "条码不能为空")
        String barcode,
        String locationCode,
        String status
) {
}
