package org.example.ride.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConstants {
    public static final String BOOTSTRAP_SERVERS_CONFIG = "localhost:9092";
    public static final String PASSENGER_NOTIFICATION_TOPIC = "passenger-notification-topic";
    public static final Integer NUM_PARTITIONS = 3;
    public static final Short REPLICATION_FACTOR = 1;
}
