package dev.alancss.customer;

import dev.alancss.exception.DuplicateResourceException;
import dev.alancss.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
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
}
