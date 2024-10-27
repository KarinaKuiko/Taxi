package org.example.rating.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConstants {
    public static final String BOOTSTRAP_SERVERS_CONFIG = "localhost:9092";
    public static final String PASSENGER_RATING_NOTIFICATION_TOPIC = "passenger-rating-notification-topic";
    public static final String DRIVER_RATING_NOTIFICATION_TOPIC = "driver-rating-notification-topic";
    public static final Integer NUM_PARTITIONS = 3;
    public static final Short REPLICATION_FACTOR = 1;
}
