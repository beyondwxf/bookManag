package com.codex.library.dto.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookSaveRequest(
        @NotNull(message = "分类不能为空")
        Long categoryId,
        @NotBlank(message = "ISBN 不能为空")
        String isbn,
        @NotBlank(message = "书名不能为空")
        String title,
        @NotBlank(message = "作者不能为空")
        String author,
        String publisher,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate publishDate,
        String description
) {
}
