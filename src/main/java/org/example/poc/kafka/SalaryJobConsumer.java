package org.example.poc.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.poc.kafka.event.SalaryGenerateRequestedEvent;
import org.example.poc.service.SalaryJobService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryJobConsumer {
    private final SalaryJobService salaryJobService;
    private final SalaryJobDlqProducer salaryJobDlqProducer;

    @KafkaListener(topics = "${app.kafka.topics.salary-generate-requested}")
    public void consumeGenerateMonthRequested(
            SalaryGenerateRequestedEvent event,
            @Header(name = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
            @Header(name = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(name = KafkaHeaders.OFFSET, required = false) Long offset,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        if (event == null || event.getJobId() == null) {
            log.warn("Skip salary-generate message because payload is invalid");
            return;
        }
        try {
            salaryJobService.processGenerateMonthJob(
                    event.getJobId(),
                    event.getMonth(),
                    event.getYear(),
                    event.isOverwriteDraft()
            );
        } catch (RuntimeException ex) {
            log.error("Salary job failed, publish to DLQ. jobId={}, key={}, offset={}", event.getJobId(), key, offset, ex);
            salaryJobDlqProducer.publish(event, topic, partition, offset, key, ex);
        }
    }
}
