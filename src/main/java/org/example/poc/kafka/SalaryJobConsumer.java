package org.example.poc.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.poc.kafka.event.SalaryGenerateRequestedEvent;
import org.example.poc.service.SalaryJobService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryJobConsumer {
    private final SalaryJobService salaryJobService;

    @KafkaListener(topics = "${app.kafka.topics.salary-generate-requested}")
    public void consumeGenerateMonthRequested(SalaryGenerateRequestedEvent event) {
        if (event == null || event.getJobId() == null) {
            log.warn("Skip salary-generate message because payload is invalid");
            return;
        }
        salaryJobService.processGenerateMonthJob(
                event.getJobId(),
                event.getMonth(),
                event.getYear(),
                event.isOverwriteDraft()
        );
    }
}
