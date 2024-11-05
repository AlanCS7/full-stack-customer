package dev.alancss.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Customer name");
        when(resultSet.getString("email")).thenReturn("customer@mail.com");
        when(resultSet.getInt("age")).thenReturn(20);
        when(resultSet.getString("gender")).thenReturn("MALE");

        // When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        // Then
        var expected = new Customer(1, "Customer name", "customer@mail.com", 20, Gender.MALE);
        assertThat(actual).isEqualTo(expected);
    }
}