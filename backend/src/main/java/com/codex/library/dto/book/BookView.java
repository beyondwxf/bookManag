package com.codex.library.dto.book;

import java.time.LocalDate;

public record BookView(
        Long id,
        Long categoryId,
        String categoryName,
        String isbn,
        String title,
        String author,
        String publisher,
        LocalDate publishDate,
        String description,
        long totalCopies,
        long availableCopies
) {
}
