package org.example.poc.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryGenerateRequestedDeadLetterEvent {
    private String originalTopic;
    private Integer originalPartition;
    private Long originalOffset;
    private String originalKey;
    private Long failedAtEpochMs;
    private String errorType;
    private String errorMessage;
    private SalaryGenerateRequestedEvent payload;
}
