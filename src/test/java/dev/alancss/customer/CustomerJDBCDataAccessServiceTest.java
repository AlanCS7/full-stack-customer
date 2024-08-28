package dev.alancss.customer;

import dev.alancss.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    private CustomerJDBCDataAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(getJdbcTemplate(), customerRowMapper);
    }

    @Test
    void shouldReturnAllCustomersWhenCustomersExist() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insert(customer);

        // When
        var customers = underTest.findAll();

        // Then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void shouldReturnCustomerWhenIdExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actualCustomer = underTest.findById(id);

        // Then
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        // Given
        int id = -1;

        // When
        var actualCustomer = underTest.findById(id);

        // Then
        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void shouldInsertNewCustomerWhenCustomerIsValid() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);

        // When
        underTest.insert(customer);

        // Then
        var customers = underTest.findAll();
        assertThat(customers)
                .extracting(Customer::getEmail)
                .contains(email);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        var customerWithEmailDuplicated = new Customer(FAKER.name().fullName(), email, 20);

        // Then
        assertThatThrownBy(() -> underTest.insert(customerWithEmailDuplicated))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

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

    @Test
    void shouldReturnTrueWhenIdExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        Integer id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean result = underTest.existsById(id);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIdDoesNotExists() {
        // Given
        int id = -1;

        // When
        boolean result = underTest.existsById(id);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldDeleteCustomerWhenIdExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteById(id);

        // Then
        var actualCustomer = underTest.findById(id);
        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void shouldUpdateAllPropertiesCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = FAKER.name().fullName();

        // When
        var update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.update(update);

        // Then
        var actualCustomer = underTest.findById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void shouldUpdateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = FAKER.name().fullName();
        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        int newAge = 21;

        // When
        var update = new Customer();
        update.setId(id);
        update.setName(newName);
        update.setEmail(newEmail);
        update.setAge(newAge);

        underTest.update(update);

        // Then
        var actualCustomer = underTest.findById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void shouldUpdateCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.update(update);

        // Then
        var actualCustomer = underTest.findById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void shouldUpdateCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = 21;

        // When
        var update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.update(update);

        // Then
        var actualCustomer = underTest.findById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void shouldNotUpdateWhenNothingChanged() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insert(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var update = new Customer();
        update.setId(id);

        underTest.update(update);

        // Then
        var actualCustomer = underTest.findById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }
}