package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.poc.entity.SalaryJob;
import org.example.poc.entity.SalaryJobStatus;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.kafka.SalaryJobProducer;
import org.example.poc.kafka.event.SalaryGenerateRequestedEvent;
import org.example.poc.repository.SalaryJobRepository;
import org.example.poc.service.SalaryJobService;
import org.example.poc.service.SalaryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryJobServiceImpl implements SalaryJobService {
    private final SalaryJobRepository salaryJobRepository;
    private final SalaryService salaryService;
    private final SalaryJobProducer salaryJobProducer;

    @Override
    @Transactional
    public SalaryJob submitGenerateMonthJob(Integer month, Integer year, boolean overwriteDraft) {
        validatePeriod(month, year);

        SalaryJob job = salaryJobRepository.save(SalaryJob.pending(month, year, overwriteDraft));
        SalaryGenerateRequestedEvent event = new SalaryGenerateRequestedEvent(
                job.getId(),
                job.getJobKey(),
                month,
                year,
                overwriteDraft,
                Instant.now().toEpochMilli()
        );

        try {
            salaryJobProducer.publishGenerateMonthRequested(event);
        } catch (RuntimeException ex) {
            job.setStatus(SalaryJobStatus.FAILED);
            job.setErrorMessage("Cannot publish Kafka event: " + ex.getMessage());
            job.setCompletedAt(Instant.now());
            salaryJobRepository.save(job);
            throw new IllegalStateException("Cannot publish Kafka event", ex);
        }

        return job;
    }

    @Override
    public Optional<SalaryJob> findById(Long id) {
        return salaryJobRepository.findById(id);
    }

    @Override
    @Transactional
    public void processGenerateMonthJob(Long jobId, Integer month, Integer year, boolean overwriteDraft) {
        SalaryJob job = salaryJobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundExeption("Can not find salary job with id: " + jobId));

        if (job.getStatus() == SalaryJobStatus.COMPLETED) {
            log.info("Skip salary job {} because it is already completed", jobId);
            return;
        }

        job.setStatus(SalaryJobStatus.PROCESSING);
        job.setStartedAt(Instant.now());
        job.setAttemptCount((job.getAttemptCount() == null ? 0 : job.getAttemptCount()) + 1);
        job.setErrorMessage(null);
        salaryJobRepository.save(job);

        try {
            int changedRows = salaryService.generateMonthFromAttendance(month, year, overwriteDraft);
            job.setAffectedRows(changedRows);
            job.setStatus(SalaryJobStatus.COMPLETED);
            job.setCompletedAt(Instant.now());
            salaryJobRepository.save(job);
        } catch (RuntimeException ex) {
            job.setStatus(SalaryJobStatus.FAILED);
            job.setErrorMessage(trimMessage(ex.getMessage()));
            job.setCompletedAt(Instant.now());
            salaryJobRepository.save(job);
            throw ex;
        }
    }

    private void validatePeriod(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 2000 || year > 3000) {
            throw new IllegalArgumentException("Year must be between 2000 and 3000");
        }
    }

    private String trimMessage(String message) {
        if (message == null || message.isBlank()) {
            return "Unknown processing error";
        }
        int maxLength = 980;
        if (message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength);
    }
}
