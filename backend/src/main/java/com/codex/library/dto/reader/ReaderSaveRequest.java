package com.codex.library.dto.reader;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReaderSaveRequest(
        Long userId,
        String username,
        String password,
        @NotBlank(message = "读者姓名不能为空")
        String name,
        String phone,
        @Email(message = "邮箱格式错误")
        String email,
        String idCard,
        String status
) {
}
