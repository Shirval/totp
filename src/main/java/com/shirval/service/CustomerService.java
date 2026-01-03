package com.shirval.service;

import com.shirval.job.CodeJobManager;
import com.shirval.model.Code;
import com.shirval.model.Customer;
import com.shirval.repository.CodeRepository;
import com.shirval.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CodeRepository codeRepository;
    private final CodeJobManager jobManager;
    private final TransactionTemplate transactionTemplate;

    public static final int CODE_LENGTH = 6;
    public static final long CODE_TTL_SECONDS = 15;

    public CustomerService(
            CustomerRepository customerRepository,
            CodeRepository codeRepository,
            CodeJobManager jobManager, TransactionTemplate transactionTemplate
    ) {
        this.customerRepository = customerRepository;
        this.codeRepository = codeRepository;
        this.jobManager = jobManager;
        this.transactionTemplate = transactionTemplate;
    }

    public Customer createCustomer() {
        Customer customer = transactionTemplate.execute((_) -> {
            Customer c = customerRepository.create();
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            Instant validTill = now.plusSeconds(CODE_TTL_SECONDS);
            Code code = new Code(c.id(), CodeGenerator.generateNumSeq(CODE_LENGTH), now, validTill);
            codeRepository.create(code);
            customerRepository.updateLastValidTill(c.id(), code.validTill());
            return new Customer(c.id(), code.validTill());
        });
        jobManager.runJob(customer.id(), customer.lastValidTill());
        return customer;
    }
}
