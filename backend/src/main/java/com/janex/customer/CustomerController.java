package com.janex.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {


    private final CustomerService customerService;


    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }


    @GetMapping("{customerId}")
    public Customer getCustomer(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomer(customerId);
    }


    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }


    @DeleteMapping("{customerId}")
    public void deleteCustomerById(@PathVariable("customerId") Long customerId) {
        customerService.deleteCustomerById(customerId);
    }


    @PutMapping("{customerId}")
    public void updateCustomerById(
            @PathVariable("customerId") Long customerId,
            @RequestBody CustomerUpdateRequest customerUpdateRequest
    ) {
        customerService.updateCustomer(customerId, customerUpdateRequest);
    }

}
