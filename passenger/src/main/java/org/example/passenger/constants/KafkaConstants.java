package org.example.passenger.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConstants {
    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String PASSENGER_NOTIFICATION_TOPIC = "passenger-notification-topic";
    public static final String PASSENGER_RATING_NOTIFICATION_TOPIC = "passenger-rating-notification-topic";
    public static final String GROUP_ID = "passenger-group";
    public static final Integer NUM_PARTITIONS = 3;
    public static final Short REPLICATION_FACTOR = 1;

}
