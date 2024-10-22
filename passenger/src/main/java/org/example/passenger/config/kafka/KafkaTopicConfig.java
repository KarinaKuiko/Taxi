package org.example.passenger.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.example.passenger.constants.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic addPassengerTopic() {
        return new NewTopic(AppConstants.PASSENGER_NOTIFICATION_TOPIC, 3, (short) 1);
    }
}
