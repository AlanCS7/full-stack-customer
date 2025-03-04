package dev.alancss.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> findAllCustomers() {
        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer;
                """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> findCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                WHERE id = ?;
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, password, age, gender)
                VALUES (?, ?, ?, ?, ?);
                """;

        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getPassword(), customer.getAge(), customer.getGender().name());
    }

    @Override
    public boolean existsCustomerByEmail(String email) {
        var sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE email = ?;
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerById(Integer id) {
        var sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE id = ?;
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?;
                """;

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        var params = new ArrayList<>();
        var sql = new StringBuilder("UPDATE customer SET ");
        boolean hasUpdates = false;

        if (customer.getName() != null) {
            sql.append("name = ?, ");
            params.add(customer.getName());
            hasUpdates = true;
        }

        if (customer.getEmail() != null) {
            sql.append("email = ?, ");
            params.add(customer.getEmail());
            hasUpdates = true;
        }

        if (customer.getAge() != null) {
            sql.append("age = ? ");
            params.add(customer.getAge());
            hasUpdates = true;
        }

        if (!hasUpdates) {
            return;
        }

        if (sql.toString().endsWith(", ")) {
            sql.setLength(sql.length() - 2);
        }

        sql.append(" WHERE id = ?;");
        params.add(customer.getId());

        jdbcTemplate.update(sql.toString(), params.toArray());
    }

    @Override
    public Optional<Customer> findUserByEmail(String email) {
        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                WHERE email = ?;
                """;

        return jdbcTemplate.query(sql, customerRowMapper, email).stream().findFirst();
    }
}
