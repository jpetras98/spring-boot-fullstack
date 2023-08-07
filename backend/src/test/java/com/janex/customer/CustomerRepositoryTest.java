package com.janex.customer;

import com.janex.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// For testing JPA
@DataJpaTest
// Do not connect to real database, instead connect to TestContainer via AbstractTestcontainers where we configured it
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository customerRepository; // Under test


    @BeforeEach
    void setUp() {
        // Delete all customers if some exists
        customerRepository.deleteAll();
    }


    @Test
    void existsCustomerByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerRepository.save(customer);

        // When
        var actual = customerRepository.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }


    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var actual = customerRepository.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isFalse();
    }


    @Test
    void existsCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerRepository.save(customer);

        Long id = customerRepository.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = customerRepository.existsCustomerById(id);

        //Then
        assertThat(actual).isTrue();
    }


    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        // Given
        Long id = -1L;

        // When
        var actual = customerRepository.existsCustomerById(id);

        //Then
        assertThat(actual).isFalse();
    }
}