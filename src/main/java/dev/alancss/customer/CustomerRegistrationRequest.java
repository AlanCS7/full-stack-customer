package dev.alancss.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}