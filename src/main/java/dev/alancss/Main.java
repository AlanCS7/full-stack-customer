package dev.alancss;

import com.github.javafaker.Faker;
import dev.alancss.customer.Customer;
import dev.alancss.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            var faker = new Faker();
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            var random = new Random();

            var customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com",
                    random.nextInt(16, 99)
            );

            customerRepository.save(customer);
        };
    }
}