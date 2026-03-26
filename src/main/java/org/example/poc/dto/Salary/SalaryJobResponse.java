package org.example.poc.dto.Salary;

import lombok.Builder;
import lombok.Getter;
import org.example.poc.entity.SalaryJob;
import org.example.poc.entity.SalaryJobStatus;

import java.time.Instant;

@Getter
@Builder
public class SalaryJobResponse {
    private Long id;
    private String jobKey;
    private Integer month;
    private Integer year;
    private boolean overwriteDraft;
    private SalaryJobStatus status;
    private Integer attemptCount;
    private Integer affectedRows;
    private String errorMessage;
    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant updatedAt;

    public static SalaryJobResponse from(SalaryJob job) {
        return SalaryJobResponse.builder()
                .id(job.getId())
                .jobKey(job.getJobKey())
                .month(job.getMonth())
                .year(job.getYear())
                .overwriteDraft(job.isOverwriteDraft())
                .status(job.getStatus())
                .attemptCount(job.getAttemptCount())
                .affectedRows(job.getAffectedRows())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
