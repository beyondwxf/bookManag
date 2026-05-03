package com.codex.library.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategorySaveRequest(
        @NotBlank(message = "分类名称不能为空")
        String name,
        @NotBlank(message = "分类编码不能为空")
        String code,
        Integer sortOrder,
        String description
) {
}
