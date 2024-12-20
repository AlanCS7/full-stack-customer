package dev.alancss.customer;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        Gender gender,
        List<String> roles
) {
}
