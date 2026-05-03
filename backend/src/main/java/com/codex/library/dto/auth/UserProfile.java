package com.codex.library.dto.auth;

public record UserProfile(
        Long id,
        String username,
        String displayName,
        String roleCode,
        String roleName,
        Long readerId
) {
}
