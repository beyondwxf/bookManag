package com.codex.library.dto.reader;

public record ReaderView(
        Long id,
        Long userId,
        String readerNo,
        String username,
        String name,
        String phone,
        String email,
        String idCard,
        String status,
        long currentLoanCount
) {
}
