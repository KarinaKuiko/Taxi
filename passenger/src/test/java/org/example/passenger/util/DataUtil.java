package org.example.passenger.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.entity.Passenger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUtil {
    public static final String URL = "/api/v1/passengers";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final Long DEFAULT_ID = 1L;
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;
    public static final String MESSAGE = "message";
    public static final String HOST_PORT = "http://localhost:8082";
    public static final String BASE_URL = HOST_PORT + URL;
    public static final String BASE_URL_WITH_ID = HOST_PORT + URL_WITH_ID;

    //Exception
    public static final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE = "Passenger was not found";
    public static final String PASSENGER_DUPlICATED_EMAIL_MESSAGE = "Passenger with this email already exists";


    //Passenger
    public static final String DEFAULT_NAME = "passenger";
    public static final String DEFAULT_EMAIL = "passenger@gmail.com";
    public static final String DEFAULT_PHONE = "+375441234567";
    public static final double DEFAULT_RATING = 5.0;


    public static PassengerReadDto.PassengerReadDtoBuilder getPassengerReadDtoBuilder() {
        return PassengerReadDto.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING);
    }

    public static PassengerCreateEditDto.PassengerCreateEditDtoBuilder getPassengerCreateEditDtoBuilder() {
        return PassengerCreateEditDto.builder()
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE);
    }

    public static Passenger.PassengerBuilder getPassengerBuilder () {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING);
    }
}
