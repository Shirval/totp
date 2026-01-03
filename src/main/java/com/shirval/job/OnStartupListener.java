package com.shirval.job;

import com.shirval.repository.CustomerRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OnStartupListener {

    private final CustomerRepository customerRepository;
    private final CodeJobManager jobManager;

    public OnStartupListener(CustomerRepository customerRepository, CodeJobManager jobManager,
                             MeterRegistry meterRegistry
    ) {
        this.customerRepository = customerRepository;
        this.jobManager = jobManager;
    }

    @EventListener
    @Timed("startup_job")
    public void onApplicationStart(ApplicationStartedEvent event) {
        customerRepository.getAll() // TODO Make batching
                .forEach((customer) -> {
                    Instant lastValidTill = Instant.now();
                    if (customer.lastValidTill().compareTo(lastValidTill) > 0) {
                        lastValidTill = customer.lastValidTill();
                    }
                    jobManager.runJob(customer.id(), lastValidTill);
                });
    }
}
