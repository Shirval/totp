package com.shirval.repository;

import com.shirval.CommonIntegrationTest;
import com.shirval.model.Customer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomerRepositoryTest extends CommonIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Disabled
    public void getAllTest() throws InterruptedException {
        List<Customer> all = customerRepository.getAll();
        System.out.println(all.size());
        Thread.sleep(60_000);
    }

    @Test
    public void shouldCreateCustomer() {
        Customer customer = customerRepository.create();
        assertNotNull(customer);
        System.out.println(customer.id());
        customerRepository.remove(customer.id());
    }

    @Test
    public void shouldUpdateLastValidTill() {
        Customer customer = customerRepository.create();
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        customerRepository.updateLastValidTill(customer.id(), now);
        customer = customerRepository.getById(customer.id());
        assertEquals(now, customer.lastValidTill());
        customerRepository.remove(customer.id());
    }
}
