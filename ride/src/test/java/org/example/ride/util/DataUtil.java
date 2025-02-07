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

    public static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJSX1IzeDRpNDBuaS0zSXdWVE1kcWx3QThLZTU2VXlUeWxNVjVzMXhpVUtJIn0.eyJleHAiOjE3Mzg4NjY0NDQsImlhdCI6MTczODg2NjE0NCwianRpIjoiY2NlMzc0YzEtNzI1Ny00MjE2LTlhZTUtMjY0YjlkZGRhOGZkIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjg0ODQvcmVhbG1zL3RheGlfcmVhbG0iLCJhdWQiOlsicmVhbG0tbWFuYWdlbWVudCIsImFjY291bnQiXSwic3ViIjoiNTBjNjE4MWEtYWQ3OS00MjY3LTk0NTItN2Q5MDZiZmViZTYyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidGF4aV9jbGllbnQiLCJzaWQiOiJmNTAyOGY2Yy00MGJmLTQyMmItYjc1Ny1hNTM1NDM0M2MzOTEiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsIkRSSVZFUiIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy10YXhpX3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJtYW5hZ2UtdXNlcnMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJnZW5kZXIiOiJNQUxFIiwibmFtZSI6ImRyaXZlciBkcml2ZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJkcml2ZXIxMUBnbWFpbC5jb20iLCJnaXZlbl9uYW1lIjoiZHJpdmVyIiwiZmFtaWx5X25hbWUiOiJkcml2ZXIiLCJlbWFpbCI6ImRyaXZlcjExQGdtYWlsLmNvbSJ9.MAw8LevVt_IVrtJNBg95RwpIQ6D--NWW_ht3gzxrCJjaOdZJt-M8-flQn-twaO2ezk7kIIYxgxLzDcqRMFHF89zXEQzjdeRIAMc2jWWSi_On1v3t2bC4O_A9fcbEN869hNEQxC3ec1XJCaqtJcIri6tskPSpbaGEoT1uoa0jtbVde5YgYsTTolTprsuYymkfaIsp7pbvJuhr7AcHyRAZJVBoU-Npkwn-lNBDxs_5jbC_rCMv31lLV7ukKekGOcMYzc9CtO_4-APlfPhnsdaw6eQQMFaPwysB0mzrBD8fAHlmUxyY0CJDO1QaUBrfcaTs0s5NilH3IIecKayc5HbhSA";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

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
