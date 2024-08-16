package dev.alancss.customer;

import dev.alancss.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
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
}
