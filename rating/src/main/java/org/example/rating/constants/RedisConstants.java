package org.example.rating.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstants {
    public final static String DRIVER_RATE_CACHE_VALUE = "driver-rate";
    public final static String PASSENGER_RATE_CACHE_VALUE = "passenger-rate";
}
