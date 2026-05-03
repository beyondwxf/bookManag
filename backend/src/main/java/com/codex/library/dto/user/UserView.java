package com.codex.library.dto.user;

import java.time.LocalDateTime;

public record UserView(
        Long id,
        String username,
        String displayName,
        Long roleId,
        String roleName,
        String roleCode,
        String phone,
        String status,
        LocalDateTime createdAt
) {
}
