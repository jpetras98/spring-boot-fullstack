package com.janex.customer;

import com.janex.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJdbcTest => we can use this annotation if we don't want to configure getJdbcTemplate by ourselves
class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService customerJDBCDataAccessService; // Under test
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();


    // Fresh instance of CustomerJDBCDataAccessService for each test.
    @BeforeEach
    void setUp() {
        customerJDBCDataAccessService = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }


    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);

        // When
        List<Customer> actual = customerJDBCDataAccessService.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }


    @Test
    void selectCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        Long id = -1L;

        // When
        var actual = customerJDBCDataAccessService.selectCustomerById(id);

        //Then
        assertThat(actual).isEmpty();
    }


    @Test
    void insertCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    void existsCustomerWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        // When
        var actual = customerJDBCDataAccessService.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isTrue();
    }


    @Test
    void customerDoesNotExistsWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var actual = customerJDBCDataAccessService.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isFalse();
    }


    @Test
    void existsCustomerWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = customerJDBCDataAccessService.existsCustomerWithId(id);

        //Then
        assertThat(actual).isTrue();
    }


    @Test
    void customerDoesNotExistsWithId() {
        // Given
        Long id = -1L;

        // When
        var actual = customerJDBCDataAccessService.existsCustomerWithId(id);

        //Then
        assertThat(actual).isFalse();
    }


    @Test
    void deleteCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        customerJDBCDataAccessService.deleteCustomerById(id);

        //Then
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }


    @Test
    void updateCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        int updatedAge = 25;

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setAge(updatedAge);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(updatedAge);
        });
    }


    @Test
    void updateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        String updatedName = FAKER.name().fullName();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(updatedName);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    void updateCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        String updatedEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail(updatedEmail);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(updatedEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    void updateCustomerAllProperties() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        String updatedEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String updatedName = FAKER.name().fullName();
        int updatedAge = 25;

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail(updatedEmail);
        updatedCustomer.setName(updatedName);
        updatedCustomer.setAge(updatedAge);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValue(updatedCustomer);
    }


    @Test
    void updateCustomerWillNotUpdateWhenNothingToUpdate() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }
}