package org.example.ride.util;

import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.DriverReadDto;
import org.example.ride.dto.read.PassengerReadDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;

import java.math.BigDecimal;

public class DataUtil {
    public static final String URL = "/api/v1/rides";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final Long DEFAULT_ID = 1L;
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;

    public static DriverReadDto getDriverReadDto () {
        return new DriverReadDto(DEFAULT_ID, "name", "name@gmail.com", "+375441234567", "MALE", DEFAULT_ID, 5.0);
    }

    public static PassengerReadDto getPassengerReadDto() {
        return new PassengerReadDto(DEFAULT_ID, "name", "name@email.com", "+375441234567", 5.0);
    }

    public static Ride getRide() {
        return new Ride(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from",
                "to", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal("123.45"));
    }

    public static RideCreateEditDto getRideCreateEditDto() {
        return new RideCreateEditDto(DEFAULT_ID, DEFAULT_ID, "from", "to");
    }

    public static RideReadDto getRideReadDto() {
        return new RideReadDto(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from",
                "to", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal("123.45"));
    }
}
