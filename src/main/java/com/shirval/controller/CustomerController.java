package com.shirval.controller;

import com.shirval.exception.TotpException;
import com.shirval.model.Customer;
import com.shirval.repository.CustomerRepository;
import com.shirval.service.CustomerService;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Timed("create.customer")
    public Customer create() {
        return customerService.createCustomer();
    }

}
