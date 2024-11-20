package org.example.ride.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.DriverReadDto;
import org.example.ride.dto.read.PassengerReadDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUtil {
    public static final String URL = "/api/v1/rides";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final String DRIVER_STATUS = "/driver-status";
    public static final String PASSENGER_STATUS = "/passenger-status";
    public static final Long DEFAULT_ID = 1L;
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;
    public static final String DRIVER_URL = "/api/v1/drivers";
    public static final String PASSENGER_URL = "/api/v1/passengers";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MESSAGE = "message";
    public static final String HOST_PORT = "http://localhost:8083";
    public static final String BASE_URL = HOST_PORT + URL;
    public static final String BASE_URL_WITH_ID = HOST_PORT + URL_WITH_ID;

    //Exception
    public static final String RIDE_NOT_FOUND_EXCEPTION_MESSAGE = "Ride was not found";
    public static final String DRIVER_NOT_FOUND_EXCEPTION_MESSAGE = "Driver was not found";
    public static final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE = "Passenger was not found";
    public static final String INVALID_PROPOSED_STATUS_MESSAGE = "Cannot be updated to the proposed status";
    public static final String IRRELEVANT_DRIVER_STATUS = "Status cannot be changed now";

    //Ride
    public static final String DEFAULT_ADDRESS_FROM = "from";
    public static final String DEFAULT_ADDRESS_TO = "to";
    public static final String DEFAULT_COST = "123.45";
    public static final String COST_FIELD = "cost";

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
                .addressFrom(DEFAULT_ADDRESS_FROM)
                .addressTo(DEFAULT_ADDRESS_TO)
                .driverRideStatus(DriverRideStatus.ACCEPTED)
                .passengerRideStatus(PassengerRideStatus.WAITING)
                .cost(new BigDecimal(DEFAULT_COST));
    }

    public static RideCreateEditDto.RideCreateEditDtoBuilder getRideCreateEditDtoBuilder() {
        return RideCreateEditDto.builder()
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .addressFrom(DEFAULT_ADDRESS_FROM)
                .addressTo(DEFAULT_ADDRESS_TO);
    }

    public static RideReadDto.RideReadDtoBuilder getRideReadDtoBuilder() {
        return RideReadDto.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .addressFrom(DEFAULT_ADDRESS_FROM)
                .addressTo(DEFAULT_ADDRESS_TO)
                .driverRideStatus(DriverRideStatus.ACCEPTED)
                .passengerRideStatus(PassengerRideStatus.WAITING)
                .cost(new BigDecimal(DEFAULT_COST));
    }
}
