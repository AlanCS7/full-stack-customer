package dev.alancss.customer;

import dev.alancss.exception.DuplicateResourceException;
import dev.alancss.exception.RequestValidationException;
import dev.alancss.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void shouldGetAllCustomers() {
        // Given
        var expectedCustomers = List.of(
                new Customer("Customer name", "customer@mail.com", 20, Gender.MALE),
                new Customer("User name", "user@mail.com", 22, Gender.MALE)
        );
        when(customerDao.findAllCustomers()).thenReturn(expectedCustomers);

        // When
        var customers = underTest.getCustomers();

        // Then
        assertThat(customers).isEqualTo(expectedCustomers);
        verify(customerDao, times(1)).findAllCustomers();
    }

    @Test
    void shouldGetCustomerById() {
        // Given
        int customerId = 1;
        Customer expectedCustomer = new Customer("Customer name", "customer@mail.com", 20, Gender.MALE);
        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(expectedCustomer));

        // When
        Customer customer = underTest.getCustomer(customerId);

        // Then
        assertThat(customer).isEqualTo(expectedCustomer);
        verify(customerDao, times(1)).findCustomerById(customerId);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCustomerNotFound() {
        // When
        int customerId = 1;
        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.getCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with ID %d not found".formatted(customerId));

        verify(customerDao, times(1)).findCustomerById(customerId);
    }

    @Test
    void shouldAddCustomer() {
        // Given
        var request = new CustomerRegistrationRequest("Customer name", "customer@mail.com", 20, Gender.MALE);
        when(customerDao.existsCustomerByEmail(request.email())).thenReturn(false);

        // When
        underTest.addCustomer(request);

        // Then
        verify(customerDao, times(1)).existsCustomerByEmail("customer@mail.com");

        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao, times(1)).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(request);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenAddingCustomerWithExistingEmail() {
        // Given
        var request = new CustomerRegistrationRequest("Customer name", "customer@mail.com", 20, Gender.MALE);

        // When
        when(customerDao.existsCustomerByEmail(request.email())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email address already in use");

        verify(customerDao, times(1)).existsCustomerByEmail("customer@mail.com");
        verify(customerDao, never()).insertCustomer(any(Customer.class));
    }

    @Test
    void shouldRemoveCustomer() {
        // Given
        int customerId = 1;
        when(customerDao.existsCustomerById(customerId)).thenReturn(true);

        // When
        underTest.removeCustomer(customerId);

        // Then
        verify(customerDao, times(1)).existsCustomerById(customerId);
        verify(customerDao, times(1)).deleteCustomerById(customerId);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRemovingNonExistentCustomer() {
        // Given
        int customerId = 1;

        when(customerDao.existsCustomerById(customerId)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> underTest.removeCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with ID %d not found".formatted(customerId));

        verify(customerDao, times(1)).existsCustomerById(customerId);
        verify(customerDao, never()).deleteCustomerById(customerId);
    }

    @Test
    void shouldUpdateCustomer() {
        // Given
        int customerId = 1;
        var actualCustomer = new Customer(customerId, "Customer name", "customer@mail.com", 20, Gender.MALE);
        var request = new CustomerUpdateRequest("New name", "new@mail.com", 22);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(actualCustomer));
        when(customerDao.existsCustomerByEmail(request.email())).thenReturn(false);

        // When
        underTest.updateCustomer(customerId, request);

        // Then
        verify(customerDao, times(1)).findCustomerById(customerId);
        verify(customerDao, times(1)).existsCustomerByEmail("new@mail.com");

        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao, times(1)).updateCustomer(customerArgumentCaptor.capture());

        var capturedValue = customerArgumentCaptor.getValue();

        assertThat(capturedValue.getId()).isEqualTo(actualCustomer.getId());
        assertThat(capturedValue)
                .usingRecursiveComparison()
                .ignoringFields("id", "gender")
                .isEqualTo(request);
    }

    @Test
    void shouldUpdateCustomerName() {
        // Given
        int customerId = 1;
        var actualCustomer = new Customer(customerId, "Customer name", "customer@mail.com", 20, Gender.MALE);
        var request = new CustomerUpdateRequest("New name", null, null);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(actualCustomer));

        // When
        underTest.updateCustomer(customerId, request);

        // Then
        verify(customerDao, times(1)).findCustomerById(customerId);

        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao, times(1)).updateCustomer(customerArgumentCaptor.capture());

        var capturedValue = customerArgumentCaptor.getValue();

        assertThat(capturedValue.getId()).isEqualTo(actualCustomer.getId());
        assertThat(capturedValue.getName()).isEqualTo(request.name());
        assertThat(capturedValue.getEmail()).isEqualTo(actualCustomer.getEmail());
        assertThat(capturedValue.getAge()).isEqualTo(actualCustomer.getAge());
        assertThat(capturedValue.getGender()).isEqualTo(actualCustomer.getGender());
    }

    @Test
    void shouldUpdateCustomerEmail() {
        // Given
        int customerId = 1;
        var actualCustomer = new Customer(customerId, "Customer name", "customer@mail.com", 20, Gender.MALE);
        var request = new CustomerUpdateRequest(null, "new@mail.com", null);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(actualCustomer));
        when(customerDao.existsCustomerByEmail(request.email())).thenReturn(false);

        // When
        underTest.updateCustomer(customerId, request);

        // Then
        verify(customerDao, times(1)).findCustomerById(customerId);
        verify(customerDao, times(1)).existsCustomerByEmail("new@mail.com");

        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao, times(1)).updateCustomer(customerArgumentCaptor.capture());

        var capturedValue = customerArgumentCaptor.getValue();
        assertThat(capturedValue.getId()).isEqualTo(actualCustomer.getId());
        assertThat(capturedValue.getName()).isEqualTo(actualCustomer.getName());
        assertThat(capturedValue.getEmail()).isEqualTo(request.email());
        assertThat(capturedValue.getAge()).isEqualTo(actualCustomer.getAge());
        assertThat(capturedValue.getGender()).isEqualTo(actualCustomer.getGender());
    }

    @Test
    void shouldUpdateCustomerAge() {
        // Given
        int customerId = 1;
        var actualCustomer = new Customer(customerId, "Customer name", "customer@mail.com", 20, Gender.MALE);
        var request = new CustomerUpdateRequest(null, null, 22);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(actualCustomer));

        // When
        underTest.updateCustomer(customerId, request);

        // Then
        verify(customerDao, times(1)).findCustomerById(customerId);

        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao, times(1)).updateCustomer(customerArgumentCaptor.capture());

        var capturedValue = customerArgumentCaptor.getValue();

        assertThat(capturedValue.getId()).isEqualTo(actualCustomer.getId());
        assertThat(capturedValue.getName()).isEqualTo(actualCustomer.getName());
        assertThat(capturedValue.getEmail()).isEqualTo(actualCustomer.getEmail());
        assertThat(capturedValue.getAge()).isEqualTo(request.age());
        assertThat(capturedValue.getGender()).isEqualTo(actualCustomer.getGender());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCustomerDoesNotExist() {
        // Given
        int customerId = 1;
        var request = new CustomerUpdateRequest("Customer name", "new@mail.com", 22);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with ID %d not found".formatted(customerId));

        verify(customerDao, times(1)).findCustomerById(customerId);
        verify(customerDao, never()).existsCustomerByEmail("new@mail.com");
        verify(customerDao, never()).updateCustomer(any(Customer.class));
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenUpdatingToExistingEmail() {
        // Given
        int customerId = 1;
        var actualCustomer = new Customer(customerId, "Customer name", "customer@mail.com", 20, Gender.MALE);
        var request = new CustomerUpdateRequest("New name", "new@mail.com", 22);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(actualCustomer));
        when(customerDao.existsCustomerByEmail(request.email())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email address already in use");

        verify(customerDao, times(1)).findCustomerById(customerId);
        verify(customerDao, times(1)).existsCustomerByEmail("new@mail.com");
        verify(customerDao, never()).updateCustomer(any(Customer.class));
    }

    @Test
    void shouldThrowRequestValidationExceptionWhenNoChangesInUpdate() {
        // Given
        int customerId = 1;
        var actualCustomer = new Customer(customerId, "Customer name", "customer@mail.com", 20, Gender.MALE);
        var request = new CustomerUpdateRequest("Customer name", "customer@mail.com", 20);

        when(customerDao.findCustomerById(customerId)).thenReturn(Optional.of(actualCustomer));

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        verify(customerDao, times(1)).findCustomerById(customerId);
        verify(customerDao, never()).existsCustomerByEmail("customer@mail.com");
        verify(customerDao, never()).updateCustomer(any(Customer.class));
    }
}