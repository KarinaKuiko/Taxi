package org.example.rating.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.example.rating.constants.KafkaConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic addPassengerRatingTopic() {
        return new NewTopic(KafkaConstants.PASSENGER_RATING_NOTIFICATION_TOPIC, KafkaConstants.NUM_PARTITIONS, KafkaConstants.REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic addDriverRatingTopic() {
        return new NewTopic(KafkaConstants.DRIVER_RATING_NOTIFICATION_TOPIC, KafkaConstants.NUM_PARTITIONS, KafkaConstants.REPLICATION_FACTOR);
    }
}
