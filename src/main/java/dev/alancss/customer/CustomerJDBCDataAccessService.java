package dev.alancss.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Customer> findAll() {
        return List.of();
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void insert(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?);
                """;

        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsById(Integer id) {
        return false;
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void update(Customer customer) {

    }
}
