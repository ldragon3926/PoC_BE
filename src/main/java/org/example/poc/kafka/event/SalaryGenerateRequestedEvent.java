package org.example.poc.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryGenerateRequestedEvent {
    private Long jobId;
    private String jobKey;
    private Integer month;
    private Integer year;
    private boolean overwriteDraft;
    private Long requestedAtEpochMs;
}
