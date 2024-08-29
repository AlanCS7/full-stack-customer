package dev.alancss.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void findAllCustomers() {
        // When
        underTest.findAllCustomers();

        // Then
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findCustomerById() {
        // Given
        int customerId = 1;

        // When
        underTest.findCustomerById(customerId);

        // Then
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void insertCustomer() {
        // Given
        var customer = new Customer("Customer name", "customer@mail.com", 20);

        // When
        underTest.insertCustomer(customer);

        // Then
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        String email = "customer@mail.com";

        // When
        underTest.existsCustomerByEmail(email);

        // Then
        verify(customerRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsCustomerById() {
        // Given
        int customerId = 1;

        // When
        underTest.existsCustomerById(customerId);

        // Then
        verify(customerRepository, times(1)).existsById(customerId);
    }

    @Test
    void deleteCustomerById() {
        // Given
        int customerId = 1;

        // When
        underTest.deleteCustomerById(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void updateCustomer() {
        // Given
        var customer = new Customer("Customer name", "customer@mail.com", 20);

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository, times(1)).save(customer);
    }
}