package org.example.rating.util;

import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.entity.DriverRate;
import org.example.rating.entity.PassengerRate;
import org.example.rating.entity.enumeration.UserType;

import java.math.BigDecimal;

public class DataUtil {
    public static final String URL = "/api/v1/rates";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final String DRIVER_URL = URL + "/driver";
    public static final String DRIVER_URL_WITH_ID = DRIVER_URL + "/{id}";
    public static final String PASSENGER_URL = URL + "/passenger";
    public static final String PASSENGER_URL_WITH_ID = PASSENGER_URL + "/{id}";
    public static final Long DEFAULT_ID = 1L;
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;

    public static RateReadDto getDriverRateReadDto() {
        return new RateReadDto(DEFAULT_ID,
                DEFAULT_ID, "Good", 4, DEFAULT_ID, UserType.PASSENGER);
    }

    public static RateReadDto getPassengerRateReadDto() {
        return new RateReadDto(DEFAULT_ID,
                DEFAULT_ID, "Good", 4, DEFAULT_ID, UserType.DRIVER);
    }

    public static RateCreateEditDto getPassengerRateCreateEditDto() {
        return new RateCreateEditDto(DEFAULT_ID, "Good", 4, DEFAULT_ID, UserType.DRIVER);
    }

    public static RateCreateEditDto getDriverRateCreateEditDto() {
        return new RateCreateEditDto(DEFAULT_ID, "Good", 4, DEFAULT_ID, UserType.PASSENGER);
    }

    public static RideReadDto getRideReadDto() {
        return new RideReadDto(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from", "to",
                "ACCEPTED", "WAITING", new BigDecimal("123.45"));
    }

    public static DriverRate getDriverRate() {
        return new DriverRate(DEFAULT_ID,
                DEFAULT_ID, "Good", 4, DEFAULT_ID, UserType.PASSENGER);
    }

    public static PassengerRate getPassengerRate() {
        return new PassengerRate(DEFAULT_ID,
                DEFAULT_ID, "Good", 4, DEFAULT_ID, UserType.DRIVER);
    }
}
