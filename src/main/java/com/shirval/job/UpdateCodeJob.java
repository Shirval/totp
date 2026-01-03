package com.shirval.job;

import com.shirval.model.Code;
import com.shirval.repository.CodeRepository;
import com.shirval.repository.CustomerRepository;
import com.shirval.service.CodeGenerator;
import com.shirval.service.CustomerService;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Instant;
import java.util.Optional;

public class UpdateCodeJob implements Runnable {
    private final TransactionTemplate transactionTemplate;
    private final CustomerRepository customerRepository;
    private final CodeRepository codeRepository;
    private final int customerId;
    private Instant lastValidTill;
    private boolean interrupted = false;

    public UpdateCodeJob(
            TransactionTemplate transactionTemplate,
            CustomerRepository customerRepository,
            CodeRepository codeRepository,
            int customerId,
            Instant lastValidTill
    ) {
        this.transactionTemplate = transactionTemplate;
        this.customerRepository = customerRepository;
        this.codeRepository = codeRepository;
        this.customerId = customerId;
        this.lastValidTill = Optional.ofNullable(lastValidTill).orElse(Instant.now());
    }

    public void run() {
        //TODO Graceful shutdown
        while (!interrupted) {
            try {
                //System.out.println("Starting code job for customer " + customerId + ", lastValidTill " + lastValidTill);

                transactionTemplate.executeWithoutResult((_) -> {
                    Instant newFrom = lastValidTill;
                    Instant now = Instant.now();
                    if (now.compareTo(newFrom) > 0) {
                        newFrom = now;
                    }
                    Instant newValidTill = newFrom.plusSeconds(CustomerService.CODE_TTL_SECONDS);

                    codeRepository.create(
                            new Code(
                                    customerId,
                                    CodeGenerator.generateNumSeq(CustomerService.CODE_LENGTH),
                                    newFrom,
                                    newValidTill
                            )
                    );
                    customerRepository.updateLastValidTill(customerId, newValidTill);

                    lastValidTill = newValidTill;
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sleepUncaught(lastValidTill.toEpochMilli() - System.currentTimeMillis() - 2000);
            }
        }
    }

    public void interrupt() {
        this.interrupted = true;
    }

    private void sleepUncaught(Long timeDiff) {
        // todo недостает логики тут. мы можем упасть не вставив ничего если бд будет недоступна
        //  и тогда до следующей вставки пройдет слишком много времени.
        // Возможно вообще надо вставлять сразу несколько кодов и делать какие то промежуточные проверки. Чтобы в
        // случае недоступности БД на запись какие то коды были там.
        try {
            Thread.sleep(timeDiff);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
