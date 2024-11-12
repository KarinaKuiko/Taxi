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

    public static DriverReadDto.DriverReadDtoBuilder getDriverReadDtoBuilder() {
        return DriverReadDto.builder()
                .id(DEFAULT_ID)
                .name("name")
                .email("name@gmail.com")
                .phone("+375441234567")
                .gender("MALE")
                .carId(DEFAULT_ID)
                .rating(5.0);
    }

    public static PassengerReadDto.PassengerReadDtoBuilder getPassengerReadDtoBuilder() {
        return PassengerReadDto.builder()
                .id(DEFAULT_ID)
                .name("name")
                .email("name@gmail.com")
                .phone("+375441234567")
                .rating(5.0);
    }

    public static Ride.RideBuilder getRideBuilder() {
        return Ride.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .addressFrom("from")
                .addressTo("to")
                .driverRideStatus(DriverRideStatus.ACCEPTED)
                .passengerRideStatus(PassengerRideStatus.WAITING)
                .cost(new BigDecimal("123.45"));
    }

    public static RideCreateEditDto.RideCreateEditDtoBuilder getRideCreateEditDtoBuilder() {
        return RideCreateEditDto.builder()
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .addressFrom("from")
                .addressTo("to");
    }

    public static RideReadDto.RideReadDtoBuilder getRideReadDtoBuilder() {
        return RideReadDto.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .addressFrom("from")
                .addressTo("to")
                .driverRideStatus(DriverRideStatus.ACCEPTED)
                .passengerRideStatus(PassengerRideStatus.WAITING)
                .cost(new BigDecimal("123.45"));
    }
}
