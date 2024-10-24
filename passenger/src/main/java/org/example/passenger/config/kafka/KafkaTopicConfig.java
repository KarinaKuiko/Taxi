package org.example.passenger.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.example.passenger.constants.KafkaConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic addPassengerTopic() {
        return new NewTopic(KafkaConstants.PASSENGER_NOTIFICATION_TOPIC, KafkaConstants.NUM_PARTITIONS, KafkaConstants.REPLICATION_FACTOR);
    }
}
