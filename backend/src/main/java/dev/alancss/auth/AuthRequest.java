package dev.alancss.auth;

public record AuthRequest(
        String username,
        String password
) {
}
