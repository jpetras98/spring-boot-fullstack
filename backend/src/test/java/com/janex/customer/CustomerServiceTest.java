package com.janex.customer;

import com.janex.exception.DuplicateResourceException;
import com.janex.exception.RequestValidationException;
import com.janex.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService customerService; // Under test

    @Mock
    private CustomerDao customerDao;


    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerDao);
    }


    @Test
    void getAllCustomers() {
        // When
        customerService.getAllCustomers();

        //Then
        verify(customerDao).selectAllCustomers();
    }


    @Test
    void canGetCustomer() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = customerService.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(customer);
    }


    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        // Given
        Long id = 1L;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> customerService.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer id [%s] not found!".formatted(id));
    }


    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 25
        );

        // When
        customerService.addCustomer(request);

        //Then
        // Use captor only for complex objects, for ID we do not have to use it
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class); // Capture sent arguments

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }


    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 25
        );

        // When
        assertThatThrownBy(() -> customerService.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                        .hasMessage("Email already taken!");

        //Then
        verify(customerDao, never()).insertCustomer(any());
    }


    @Test
    void deleteCustomerById() {
        // Given
        Long id = 1L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        customerService.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomerById(id);
    }


    @Test
    void willThrowWhenIdNotExistsWhileDeletingCustomerById() {
        // Given
        Long id = 1L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> customerService.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer id [%s] not found!".formatted(id));

        //Then
        verify(customerDao, never()).deleteCustomerById(id);
    }


    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alexandro",
                "alexandro@gmail.com",
                20
        );

        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);

        // When
        customerService.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }


    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alexandro",
                null,
                null
        );

        // When
        customerService.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }


    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                "alexandro@gmail.com",
                null
        );

        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);

        // When
        customerService.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }


    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                20
        );

        // When
        customerService.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }


    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                "alexandro@gmail.com",
                null
        );

        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(true);

        // When
        assertThatThrownBy(() -> customerService.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                        .hasMessage("Email already taken!");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }


    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        Long id = 1L;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Alex");
        customer.setEmail("alex@gmail.com");
        customer.setAge(25);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        // When
        assertThatThrownBy(() -> customerService.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Request does not contain any changes!");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

}