package com.shirval.job;

import com.shirval.repository.CodeRepository;
import io.micrometer.core.annotation.Timed;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class CodeCleanJob {

    private final CodeRepository codeRepository;

    public CodeCleanJob(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Scheduled(initialDelay = 5, fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    @Timed("clean.code.job")
    public void cleanOldCodes() {
        System.out.println("Cleaning old codes");
        codeRepository.removeOlderThan(Instant.now().minusSeconds(60));
    }
}
