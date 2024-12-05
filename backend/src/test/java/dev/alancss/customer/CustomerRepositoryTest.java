package dev.alancss.customer;

import dev.alancss.AbstractTestcontainers;
import dev.alancss.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.save(customer);

        // When
        boolean result = underTest.existsByEmail(email);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean result = underTest.existsByEmail(email);

        // Then
        assertThat(result).isFalse();
    }
}