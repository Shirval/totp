package com.shirval.controller;

import com.shirval.CleanupService;
import com.shirval.CommonIntegrationTest;
import com.shirval.model.Customer;
import com.shirval.repository.CodeRepository;
import com.shirval.service.CustomerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CodeControllerTest extends CommonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CleanupService cleanupService;
    @Autowired
    private CodeRepository codeRepository;

    @Test
    public void shouldReturnCurrentCode() throws Exception {
        Customer customer = customerService.createCustomer();

        mockMvc.perform(get("/v1/code/current/byCustomerId/" + customer.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", notNullValue()));

        cleanupService.clean(customer.id());
    }

    @Test
    public void shouldReturnNotFoundIfCodeNotFound() throws Exception {
        mockMvc.perform(get("/v1/code/current/byCustomerId/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("notFound")))
                .andExpect(jsonPath("$.message", equalTo("Requested entity not found")));
    }
}