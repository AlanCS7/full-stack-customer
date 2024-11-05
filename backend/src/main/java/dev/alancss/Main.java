package dev.alancss;

import com.github.javafaker.Faker;
import dev.alancss.customer.Customer;
import dev.alancss.customer.CustomerRepository;
import dev.alancss.customer.Gender;
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
            var random = new Random();
            var faker = new Faker();
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            int age = random.nextInt(16, 99);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

            var customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com",
                    age,
                    gender
            );

            customerRepository.save(customer);
        };
    }
}