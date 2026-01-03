package com.shirval.repository;

import com.shirval.CommonIntegrationTest;
import com.shirval.model.Code;
import com.shirval.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CodeRepositoryTest extends CommonIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CodeRepository codeRepository;

    @Test
    public void shouldCreateCode() {
        Customer customer = customerRepository.create();
        Instant from = Instant.now().minusSeconds(5).truncatedTo(ChronoUnit.MILLIS);
        Instant till = from.plusSeconds(30);
        codeRepository.create(new Code(customer.id(), "12345678", from, till));

        List<Code> codes = codeRepository.getCodes(customer.id());
        assertEquals(1, codes.size());
        Code code =  codes.getFirst();
        assertEquals("12345678", code.code());
        assertEquals(from, code.validFrom());
        assertEquals(till, code.validTill());

        codeRepository.removeByCustomerId(customer.id());
        customerRepository.remove(customer.id());
    }

    @Test
    public void shouldCheckCode() {
        Customer customer = customerRepository.create();
        Instant from = Instant.now().minusSeconds(5).truncatedTo(ChronoUnit.MILLIS);
        Instant till = from.plusSeconds(30);
        codeRepository.create(new Code(customer.id(), "12345678", from, till));

        assertTrue(codeRepository.checkCode(customer.id(), "12345678"));
        assertFalse(codeRepository.checkCode(customer.id(), "87654321"));

        codeRepository.removeByCustomerId(customer.id());
        customerRepository.remove(customer.id());
    }
}
