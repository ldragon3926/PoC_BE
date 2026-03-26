package org.example.poc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.poc.repository.SalaryJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryJobCleanupScheduler {
    private final SalaryJobRepository salaryJobRepository;

    @Value("${app.salary-job.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    @Value("${app.salary-job.cleanup.retention-days:60}")
    private int retentionDays;

    @Scheduled(cron = "${app.salary-job.cleanup.cron:0 30 2 * * *}")
    @Transactional
    public void cleanupOldSalaryJobs() {
        if (!cleanupEnabled || retentionDays <= 0) {
            return;
        }

        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        int deletedRows = salaryJobRepository.deleteByCreatedAtBefore(cutoff);
        if (deletedRows > 0) {
            log.info("Cleaned {} salary job record(s) older than {} days", deletedRows, retentionDays);
        }
    }
}
