package dev.alancss.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> findAllCustomers();

    Optional<Customer> findCustomerById(Integer id);

    void insertCustomer(Customer customer);

    boolean existsCustomerByEmail(String email);

    boolean existsCustomerById(Integer id);

    void deleteCustomerById(Integer id);

    void updateCustomer(Customer customer);

    Optional<Customer> findUserByEmail(String email);
}