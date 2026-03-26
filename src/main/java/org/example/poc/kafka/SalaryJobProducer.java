package org.example.poc.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.poc.kafka.event.SalaryGenerateRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryJobProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.salary-generate-requested}")
    private String salaryGenerateRequestedTopic;

    public void publishGenerateMonthRequested(SalaryGenerateRequestedEvent event) {
        kafkaTemplate.send(salaryGenerateRequestedTopic, event.getJobKey(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Publish salary job event failed for jobId={}", event.getJobId(), ex);
                    }
                });
    }
}
