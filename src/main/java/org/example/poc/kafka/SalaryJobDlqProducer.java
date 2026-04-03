package org.example.poc.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.poc.kafka.event.SalaryGenerateRequestedDeadLetterEvent;
import org.example.poc.kafka.event.SalaryGenerateRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryJobDlqProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.salary-generate-requested-dlq}")
    private String salaryGenerateRequestedDlqTopic;

    public void publish(
            SalaryGenerateRequestedEvent payload,
            String originalTopic,
            Integer originalPartition,
            Long originalOffset,
            String originalKey,
            Exception exception) {
        SalaryGenerateRequestedDeadLetterEvent event = new SalaryGenerateRequestedDeadLetterEvent(
                originalTopic,
                originalPartition,
                originalOffset,
                originalKey,
                Instant.now().toEpochMilli(),
                exception.getClass().getName(),
                trimMessage(exception.getMessage()),
                payload
        );
        kafkaTemplate.send(salaryGenerateRequestedDlqTopic, originalKey, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Publish salary DLQ event failed for key={}", originalKey, ex);
                    }
                });
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
