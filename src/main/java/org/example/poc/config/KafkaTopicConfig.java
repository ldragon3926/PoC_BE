package org.example.poc.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic salaryGenerateRequestedTopic(
            @Value("${app.kafka.topics.salary-generate-requested}") String topicName,
            @Value("${app.kafka.topics.partitions:3}") int partitions,
            @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return TopicBuilder.name(topicName).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic salaryGenerateRequestedDlqTopic(
            @Value("${app.kafka.topics.salary-generate-requested-dlq}") String topicName,
            @Value("${app.kafka.topics.partitions:3}") int partitions,
            @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return TopicBuilder.name(topicName).partitions(partitions).replicas(replicas).build();
    }
}
