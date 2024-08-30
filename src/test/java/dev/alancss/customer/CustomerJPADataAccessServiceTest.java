package dev.alancss.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldFindAllCustomers() {
        // Given
        var expectedCustomers = List.of(
                new Customer("Customer name", "customer@mail.com", 20),
                new Customer("User name", "user@mail.com", 22)
        );
        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        // When
        List<Customer> customers = underTest.findAllCustomers();

        // Then
        assertThat(customers).isEqualTo(expectedCustomers);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldFindCustomerById() {
        // Given
        int customerId = 1;
        Customer expectedCustomer = new Customer("Customer name", "customer@mail.com", 20);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(expectedCustomer));

        // When
        Optional<Customer> customer = underTest.findCustomerById(customerId);

        // Then
        assertThat(customer).isEqualTo(Optional.of(expectedCustomer));
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldInsertCustomer() {
        // Given
        var customer = new Customer("Customer name", "customer@mail.com", 20);

        // When
        underTest.insertCustomer(customer);

        // Then
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldCheckIfCustomerExistsByEmail() {
        // Given
        String email = "customer@mail.com";
        when(customerRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean exists = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(exists).isTrue();
        verify(customerRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldCheckIfCustomerExistsById() {
        // Given
        int customerId = 1;
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        boolean exists = underTest.existsCustomerById(customerId);

        // Then
        assertThat(exists).isTrue();
        verify(customerRepository, times(1)).existsById(customerId);
    }

    @Test
    void shouldDeleteCustomerById() {
        // Given
        int customerId = 1;

        // When
        underTest.deleteCustomerById(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void shouldUpdateCustomer() {
        // Given
        var customer = new Customer("Customer name", "customer@mail.com", 20);

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository, times(1)).save(customer);
    }
}