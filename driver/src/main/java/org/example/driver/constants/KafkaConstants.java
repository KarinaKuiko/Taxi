package org.example.driver.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConstants {
    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String DRIVER_NOTIFICATION_TOPIC = "driver-notification-topic";
    public static final String GROUP_ID = "driver-group";
    public static final Integer NUM_PARTITIONS = 3;
    public static final Short REPLICATION_FACTOR = 1;
}
