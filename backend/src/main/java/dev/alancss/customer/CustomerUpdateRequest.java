package dev.alancss.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        String password,
        Integer age
) {
}
