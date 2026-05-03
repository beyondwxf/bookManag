package com.codex.library.dto.bookcopy;

public record BookCopyView(
        Long id,
        Long bookId,
        String barcode,
        String locationCode,
        String status,
        Integer version,
        String bookTitle,
        String isbn,
        String author
) {
}
