package dev.alancss.customer;

import dev.alancss.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    private CustomerJDBCDataAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(getJdbcTemplate(), customerRowMapper);
    }

    @Test
    void findAll() {
        // Given

        // When

        // Then
    }

    @Test
    void findById() {
        // Given

        // When

        // Then
    }

    @Test
    void insert() {
        // Given

        // When

        // Then
    }

    @Test
    void existsByEmail() {
        // Given

        // When

        // Then
    }

    @Test
    void existsById() {
        // Given

        // When

        // Then
    }

    @Test
    void deleteById() {
        // Given

        // When

        // Then
    }

    @Test
    void update() {
        // Given

        // When

        // Then
    }
}