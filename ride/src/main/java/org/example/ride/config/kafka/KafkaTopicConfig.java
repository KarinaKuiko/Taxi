package org.example.ride.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.example.ride.constants.KafkaConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic addPassengerTopic() {
        return new NewTopic(KafkaConstants.PASSENGER_NOTIFICATION_TOPIC, KafkaConstants.NUM_PARTITIONS, KafkaConstants.REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic addDriverTopic() {
        return new NewTopic(KafkaConstants.DRIVER_NOTIFICATION_TOPIC, KafkaConstants.NUM_PARTITIONS, KafkaConstants.REPLICATION_FACTOR);
    }
}
