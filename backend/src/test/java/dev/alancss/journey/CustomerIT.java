package dev.alancss.journey;

import com.github.javafaker.Faker;
import dev.alancss.customer.Customer;
import dev.alancss.customer.CustomerRegistrationRequest;
import dev.alancss.customer.CustomerUpdateRequest;
import dev.alancss.customer.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {

    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "api/v1/customers";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRegisterCustomer_whenValidRequestIsProvided() {
        // Arrange
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(18, 99);

        var request = new CustomerRegistrationRequest(name, email, age, Gender.MALE);

        // Act - Register the customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        // Assert - Retrieve and verify the customer list
        List<Customer> customers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customers).isNotNull();

        // Assert - Verify the customer is in the list
        Customer expectedCustomer = new Customer(name, email, age, Gender.MALE);
        assertThat(customers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);
    }

    @Test
    void shouldGetCustomerById_whenValidIdIsProvided() {
        // Arrange
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(18, 99);

        var request = new CustomerRegistrationRequest(name, email, age, Gender.MALE);

        // Act - Register the customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        // Assert - Retrieve and verify the customer list
        List<Customer> customers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customers).isNotNull();

        // Extract the ID of the newly registered customer
        int registeredCustomerId = customers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected customer not found in the list."));

        // Act & Assert - Retrieve the customer by ID
        Customer retrievedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", registeredCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(retrievedCustomer).isNotNull();
        assertThat(retrievedCustomer.getId()).isEqualTo(registeredCustomerId);
        assertThat(retrievedCustomer.getName()).isEqualTo(name);
        assertThat(retrievedCustomer.getEmail()).isEqualTo(email);
        assertThat(retrievedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void shouldDeleteCustomer_whenValidIdIsProvided() {
        // Arrange
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(18, 99);

        var request = new CustomerRegistrationRequest(name, email, age, Gender.MALE);

        // Act & Assert - Register the customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        // Act - Retrieve the list of customers
        List<Customer> customers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customers).isNotNull();

        // Extract the ID of the newly registered customer
        int registeredCustomerId = customers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected customer not found in the list."));

        // Act & Assert - Delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", registeredCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // Act & Assert - Verify the customer no longer exists
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", registeredCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldUpdateCustomer_whenValidUpdateRequestIsProvided() {
        // Arrange
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(18, 99);

        var request = new CustomerRegistrationRequest(name, email, age, Gender.MALE);

        // Act & Assert - Register the customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        // Act - Retrieve the list of customers
        List<Customer> customers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customers).isNotNull();

        // Extract the ID of the newly registered customer
        int registeredCustomerId = customers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected customer not found in the list."));

        // Act & Assert - Update the customer
        String newName = "New Name";
        var updateRequest = new CustomerUpdateRequest(newName, null, null);

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", registeredCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk();

        // Act & Assert - Verify the customer was updated
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", registeredCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedCustomer).isNotNull();

        // Create expected updated customer object
        Customer expectedUpdatedCustomer = new Customer(registeredCustomerId, newName, email, age, Gender.MALE);

        // Assert using recursive comparison to check updated fields while ignoring "id"
        assertThat(updatedCustomer)
                .usingRecursiveComparison()
                .isEqualTo(expectedUpdatedCustomer);
    }

}
