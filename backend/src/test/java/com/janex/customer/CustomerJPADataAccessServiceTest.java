package com.janex.customer;

import com.janex.AbstractTestcontainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJPADataAccessService customerJPADataAccessService; // Under test

    @Mock
    private CustomerRepository customerRepository;

    // We can use annotation @ExtendWith(MockitoExtension.class) instead of this code
    // Check CustomerServiceTest
    private AutoCloseable autoCloseable;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customerJPADataAccessService = new CustomerJPADataAccessService(customerRepository);
    }


    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // When
        customerJPADataAccessService.selectAllCustomers();

        //Then
        verify(customerRepository).findAll();
    }


    @Test
    void selectCustomerById() {
        // Given
        Long id = 1L;

        // When
        customerJPADataAccessService.selectCustomerById(id);

        //Then
        verify(customerRepository).findById(id);
    }


    @Test
    void insertCustomer() {
        // Given
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName(FAKER.name().fullName());
        customer.setEmail(FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID());
        customer.setAge(20);

        // When
        customerJPADataAccessService.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }


    @Test
    void existsCustomerWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        customerJPADataAccessService.existsCustomerWithEmail(email);

        //Then
        verify(customerRepository).existsCustomerByEmail(email);
    }


    @Test
    void existsCustomerWithId() {
        // Given
        Long id = 1L;

        // When
        customerJPADataAccessService.existsCustomerWithId(id);

        //Then
        verify(customerRepository).existsCustomerById(id);
    }


    @Test
    void deleteCustomerById() {
        // Given
        Long id = 1L;

        // When
        customerJPADataAccessService.deleteCustomerById(id);

        //Then
        verify(customerRepository).deleteById(id);
    }


    @Test
    void updateCustomer() {
        // Given
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName(FAKER.name().fullName());
        customer.setEmail(FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID());
        customer.setAge(20);

        // When
        customerJPADataAccessService.updateCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }
}