package dev.alancss.journey;

import com.github.javafaker.Faker;
import dev.alancss.auth.AuthRequest;
import dev.alancss.auth.AuthResponse;
import dev.alancss.customer.CustomerDTO;
import dev.alancss.customer.CustomerRegistrationRequest;
import dev.alancss.customer.Gender;
import dev.alancss.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIT {

    private static final Random RANDOM = new Random();
    private static final String AUTH_PATH = "api/v1/auth";
    private static final String CUSTOMER_PATH = "api/v1/customers";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void canLogin() {
        // Arrange
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(18, 99);
        Gender gender = Gender.MALE;
        String password = "password";

        var customerRegistrationRequest = new CustomerRegistrationRequest(name, email, password, age, gender);

        var authRequest = new AuthRequest(email, password);

        webTestClient.post()
                .uri(AUTH_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequest)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // Act - Register the customer
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerRegistrationRequest)
                .exchange()
                .expectStatus().isCreated();

        var result = webTestClient.post()
                .uri(AUTH_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthResponse>() {
                })
                .returnResult();

        String jwtToken = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        var responseBody = result.getResponseBody();
        CustomerDTO customerDTO = responseBody.customer();

        assertThat(jwtUtil.isTokenValid(jwtToken, customerDTO.email())).isTrue();

        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }
}
