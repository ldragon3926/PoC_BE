package org.example.poc.service;

import org.example.poc.entity.SalaryJob;

import java.util.Optional;

public interface SalaryJobService {
    SalaryJob submitGenerateMonthJob(Integer month, Integer year, boolean overwriteDraft);
    Optional<SalaryJob> findById(Long id);
    void processGenerateMonthJob(Long jobId, Integer month, Integer year, boolean overwriteDraft);
}
