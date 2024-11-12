package org.example.passenger.util;

import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.entity.Passenger;

public class DataUtil {
    public static final String URL = "/api/v1/passengers";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final Long DEFAULT_ID = 1L;
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;

    public static PassengerReadDto.PassengerReadDtoBuilder getPassengerReadDtoBuilder() {
        return PassengerReadDto.builder()
                .id(DEFAULT_ID)
                .name("passenger")
                .email("passenger@gmail.com")
                .phone("+375441234567")
                .rating(5.0);
    }

    public static PassengerCreateEditDto.PassengerCreateEditDtoBuilder getPassengerCreateEditDtoBuilder() {
        return PassengerCreateEditDto.builder()
                .name("passenger")
                .email("passenger@gmail.com")
                .phone("+375441234567");
    }

    public static Passenger.PassengerBuilder getPassengerBuilder() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name("passenger")
                .email("passenger@gmail.com")
                .phone("+375441234567")
                .rating(5.0);
    }
}
