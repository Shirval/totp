package com.shirval.job;

import com.shirval.repository.CodeRepository;
import com.shirval.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class CodeJobManager {

    private final Map<Integer, Future> jobs = new ConcurrentHashMap<>();
    private final CustomerRepository customerRepository;
    private final CodeRepository codeRepository;
    private final TransactionTemplate transactionTemplate;
    private final ExecutorService vThreadExecutor;

    public CodeJobManager(
            CustomerRepository customerRepository,
            CodeRepository codeRepository,
            TransactionTemplate transactionTemplate
    ) {
        this.customerRepository = customerRepository;
        this.codeRepository = codeRepository;
        this.transactionTemplate = transactionTemplate;

        this.vThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    }

    public void runJob(int customerId, Instant lastValidTill) {
        //TODO Graceful shutdown
        UpdateCodeJob job = new UpdateCodeJob(
                transactionTemplate,
                customerRepository,
                codeRepository,
                customerId,
                lastValidTill
        );
        jobs.computeIfAbsent(customerId, (_) -> vThreadExecutor.submit(job));
    }
}
