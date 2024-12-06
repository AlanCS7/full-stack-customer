package dev.alancss.customer;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        Gender gender
) {
}
