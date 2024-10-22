package org.example.passenger.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    public static final String PASSENGER_NOT_FOUND = "passenger.not.found";
    public static final String PASSENGER_DUPlICATED_EMAIL = "passenger.email.duplicated";

    public static final String INTERNAL_SERVER_ERROR = "Something went wrong. Please, try again later";

    public static final String PHONE_PATTERN = "^(\\+375(29|33|44|25|17|16|15|44|29|33|44)?[0-9]{7}|80(29|33|44|25|17|16|15|44)?[0-9]{7})$";

    //Kafka
    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String PASSENGER_NOTIFICATION_TOPIC = "passenger-notification-topic";
    public static final String GROUP_ID = "passenger-group";
}
