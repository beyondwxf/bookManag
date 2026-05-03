package com.codex.library.dto.auth;

public record LoginResponse(String token, UserProfile user) {
}
