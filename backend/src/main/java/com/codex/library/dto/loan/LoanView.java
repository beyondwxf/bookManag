package com.codex.library.dto.loan;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanView(
        Long id,
        String loanNo,
        Long readerId,
        String readerNo,
        String readerName,
        String bookTitle,
        String barcode,
        String isbn,
        LocalDateTime borrowTime,
        LocalDate dueDate,
        LocalDateTime returnTime,
        String status,
        String borrowedByName,
        String returnedByName
) {
}
