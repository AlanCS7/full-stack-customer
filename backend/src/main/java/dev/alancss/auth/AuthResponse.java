package dev.alancss.auth;

import dev.alancss.customer.CustomerDTO;

public record AuthResponse(
        String token,
        CustomerDTO customer
) {
}
