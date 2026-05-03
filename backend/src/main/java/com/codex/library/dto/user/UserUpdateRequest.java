package com.codex.library.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank(message = "姓名不能为空")
        String displayName,
        @NotNull(message = "角色不能为空")
        Long roleId,
        String phone,
        String status
) {
}
