package com.janex.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    private final static List<Customer> customers;


    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(
                1L,
                "Alex",
                "alex@gmail.com",
                21
        );
        customers.add(alex);

        Customer jamila = new Customer(
                2L,
                "jamila",
                "jamila@gmail.com",
                19
        );
        customers.add(jamila);
    }


    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }


    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customers.stream().
                filter(customer -> customer.getId().equals(id))
                .findFirst();
    }


    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }


    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream().anyMatch(customer -> customer.getEmail().equals(email));
    }


    @Override
    public boolean existsCustomerWithId(Long id) {
        return customers.stream().anyMatch(customer -> customer.getId().equals(id));
    }


    @Override
    public void deleteCustomerById(Long id) {
        customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .ifPresent(customers::remove);
    }


    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }
}
