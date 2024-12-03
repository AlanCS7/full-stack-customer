package dev.alancss.customer;

import dev.alancss.exception.DuplicateResourceException;
import dev.alancss.exception.RequestValidationException;
import dev.alancss.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getCustomers() {
        return customerDao.findAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with ID %d not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        if (customerDao.existsCustomerByEmail(request.email())) {
            throw new DuplicateResourceException("Email address already in use");
        }

        var customer = new Customer(
                request.name(),
                request.email(),
                passwordEncoder.encode("password"),
                request.age(),
                request.gender());

        customerDao.insertCustomer(customer);
    }

    public void removeCustomer(Integer customerId) {
        if (!customerDao.existsCustomerById(customerId)) {
            throw new ResourceNotFoundException("Customer with ID %d not found".formatted(customerId));
        }
        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest request) {
        var customer = customerDao.findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with ID %d not found".formatted(customerId)
                ));

        boolean changes = false;

        if (request.name() != null && !request.name().equals(customer.getName())) {
            customer.setName(request.name());
            changes = true;
        }

        if (request.age() != null && !request.age().equals(customer.getAge())) {
            customer.setAge(request.age());
            changes = true;
        }

        if (request.email() != null && !request.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerByEmail(request.email())) {
                throw new DuplicateResourceException("Email address already in use");
            }
            customer.setEmail(request.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}
