package dev.alancss;

import dev.alancss.customer.Customer;
import dev.alancss.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            var customer = new Customer(
                    "customer",
                    "customer@mail.com",
                    20
            );

            var user = new Customer(
                    "user",
                    "user@mail.com",
                    19
            );

            customerRepository.saveAll(List.of(customer, user));
        };
    }
}