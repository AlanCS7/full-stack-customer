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
                "password", 20,
                Gender.MALE
        );
        underTest.insertCustomer(customer);

        // When
        var customers = underTest.findAllCustomers();

        // Then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void shouldReturnCustomerWhenIdExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actualCustomer = underTest.findCustomerById(id);

        // Then
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(Gender.MALE);
        });
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        // Given
        int id = -1;

        // When
        var actualCustomer = underTest.findCustomerById(id);

        // Then
        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void shouldInsertCustomerNewCustomerWhenCustomerIsValid() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);

        // When
        underTest.insertCustomer(customer);

        // Then
        var customers = underTest.findAllCustomers();
        assertThat(customers)
                .extracting(Customer::getEmail)
                .contains(email);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        var customerWithEmailDuplicated = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);

        // Then
        assertThatThrownBy(() -> underTest.insertCustomer(customerWithEmailDuplicated))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        // When
        boolean result = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean result = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenIdExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        Integer id = underTest.findAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean result = underTest.existsCustomerById(id);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIdDoesNotExists() {
        // Given
        int id = -1;

        // When
        boolean result = underTest.existsCustomerById(id);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldDeleteCustomerWhenIdExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteCustomerById(id);

        // Then
        var actualCustomer = underTest.findCustomerById(id);
        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void shouldUpdateCustomerAllPropertiesCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
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

        underTest.updateCustomer(update);

        // Then
        var actualCustomer = underTest.findCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(Gender.MALE);
        });
    }

    @Test
    void shouldUpdateCustomerCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
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

        underTest.updateCustomer(update);

        // Then
        var actualCustomer = underTest.findCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getGender()).isEqualTo(Gender.MALE);
        });
    }

    @Test
    void shouldUpdateCustomerCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
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

        underTest.updateCustomer(update);

        // Then
        var actualCustomer = underTest.findCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(Gender.MALE);
        });
    }

    @Test
    void shouldUpdateCustomerCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
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

        underTest.updateCustomer(update);

        // Then
        var actualCustomer = underTest.findCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getGender()).isEqualTo(Gender.MALE);
        });
    }

    @Test
    void shouldNotUpdateCustomerWhenNothingChanged() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(FAKER.name().fullName(), email, "password", 20, Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.findAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // Then
        var actualCustomer = underTest.findCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(Gender.MALE);
        });
    }
}