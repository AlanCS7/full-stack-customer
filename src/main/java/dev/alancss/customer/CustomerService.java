package dev.alancss.customer;

import dev.alancss.exception.DuplicateResourceException;
import dev.alancss.exception.RequestValidationException;
import dev.alancss.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getCustomers() {
        return customerDao.findAll();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with ID %d not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        if (customerDao.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email address already in use");
        }

        var customer = new Customer(request.name(), request.email(), request.age());
        customerDao.insert(customer);
    }

    public void removeCustomer(Integer customerId) {
        if (!customerDao.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer with ID %d not found".formatted(customerId));
        }
        customerDao.deleteById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest request) {
        var customer = customerDao.findById(customerId)
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
            if (customerDao.existsByEmail(request.email())) {
                throw new DuplicateResourceException("Email address already in use");
            }
            customer.setEmail(request.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

        customerDao.update(customer);
    }
}
