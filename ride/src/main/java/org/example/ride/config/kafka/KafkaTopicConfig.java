package org.example.ride.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic addPassengerTopic() {
        return new NewTopic("passenger-notification-topic", 3, (short) 1);
    }
}
