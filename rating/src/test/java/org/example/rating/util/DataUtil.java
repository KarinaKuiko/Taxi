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

    public static RateReadDto.RateReadDtoBuilder getDriverRateReadDtoBuilder() {
        return RateReadDto.builder()
                .id(DEFAULT_ID)
                .rideId(DEFAULT_ID)
                .comment("Good")
                .rating(4)
                .userId(DEFAULT_ID)
                .userType(UserType.PASSENGER);
    }

    public static RateReadDto.RateReadDtoBuilder getPassengerRateReadDtoBuilder() {
        return RateReadDto.builder()
                .id(DEFAULT_ID)
                .rideId(DEFAULT_ID)
                .comment("Good")
                .rating(4)
                .userId(DEFAULT_ID)
                .userType(UserType.DRIVER);
    }

    public static RateCreateEditDto.RateCreateEditDtoBuilder getPassengerRateCreateEditDtoBuilder() {
        return RateCreateEditDto.builder()
                .rideId(DEFAULT_ID)
                .comment("Good")
                .rating(4)
                .userId(DEFAULT_ID)
                .userType(UserType.DRIVER);
    }

    public static RateCreateEditDto.RateCreateEditDtoBuilder getDriverRateCreateEditDtoBuilder() {
        return RateCreateEditDto.builder()
                .rideId(DEFAULT_ID)
                .comment("Good")
                .rating(4)
                .userId(DEFAULT_ID)
                .userType(UserType.PASSENGER);
    }

    public static RideReadDto.RideReadDtoBuilder getRideReadDtoBuilder() {
        return RideReadDto.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .addressFrom("from")
                .addressTo("to")
                .driverRideStatus("ACCEPTED")
                .passengerRideStatus("WAITING")
                .cost(new BigDecimal("123.45"));
    }

    public static DriverRate.DriverRateBuilder getDriverRateBuilder() {
        return DriverRate.builder()
                .id(DEFAULT_ID)
                .rideId(DEFAULT_ID)
                .comment("Good")
                .rating(4)
                .userId(DEFAULT_ID)
                .userType(UserType.PASSENGER);
    }

    public static PassengerRate.PassengerRateBuilder getPassengerRateBuilder() {
        return PassengerRate.builder()
                .id(DEFAULT_ID)
                .rideId(DEFAULT_ID)
                .comment("Good")
                .rating(4)
                .userId(DEFAULT_ID)
                .userType(UserType.DRIVER);
    }
}
