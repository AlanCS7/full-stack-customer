package dev.alancss.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerListDataAccessService implements CustomerDao {

    private static final List<Customer> customers;

    static {
        customers = new ArrayList<>();

        var customer = new Customer(
                1,
                "customer",
                "customer@mail.com",
                20
        );
        customers.add(customer);

        var user = new Customer(
                2,
                "user",
                "user@mail.com",
                19
        );
        customers.add(user);
    }

    @Override
    public List<Customer> findAll() {
        return customers;
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }
}
