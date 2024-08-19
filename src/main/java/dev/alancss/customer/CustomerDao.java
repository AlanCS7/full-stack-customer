package dev.alancss.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> findAll();

    Optional<Customer> findById(Integer id);

    void insert(Customer customer);

    boolean existsByEmail(String email);

    boolean existsById(Integer id);

    void deleteById(Integer id);

    void update(Customer customer);
}