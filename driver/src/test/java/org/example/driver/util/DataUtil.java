package org.example.driver.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.entity.enumeration.Gender;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUtil {
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;
    public static final String HOST_PORT = "http://localhost:8081";
    public static final String URL = "/api/v1/{entity}";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final Long DEFAULT_ID = 1L;
    public static final String CAR_ENTITY = "cars";
    public static final String DRIVER_ENTITY = "drivers";
    public static final String MESSAGE = "message";
    public static final String BASE_URL = HOST_PORT + URL;
    public static final String BASE_URL_WITH_ID = HOST_PORT + URL_WITH_ID;

    //Exception
    public static final String CAR_NOT_FOUND = "Car was not found";
    public static final String CAR_DUPLICATED_NUMBER = "Car with this number already exists";
    public static final String DRIVER_NOT_FOUND = "Driver was not found";
    public static final String DRIVER_DUPLICATED_EMAIL = "Driver with this email already exists";

    //Car
    public static final String DEFAULT_COLOR = "red";
    public static final String DEFAULT_BRAND = "BMW";
    public static final String DEFAULT_NUMBER = "AB123CD";
    public static final int DEFAULT_YEAR = 2023;

    //Driver
    public static final String DEFAUlT_NAME = "test";
    public static final String DEFAULT_EMAIL = "test@gmail.com";
    public static final String DEFAULT_PHONE = "+375297654321";
    public static final double DEFAULT_RATING = 5.0;


    public static Car.CarBuilder getCarBuilder() {
        return Car.builder()
                .id(DEFAULT_ID)
                .color(DEFAULT_COLOR)
                .brand(DEFAULT_BRAND)
                .number(DEFAULT_NUMBER)
                .year(DEFAULT_YEAR)
                .drivers(List.of());
    }

    public static CarReadDto.CarReadDtoBuilder getCarReadDtoBuilder() {
        return CarReadDto.builder()
                .id(DEFAULT_ID)
                .color(DEFAULT_COLOR)
                .brand(DEFAULT_BRAND)
                .number(DEFAULT_NUMBER)
                .year(DEFAULT_YEAR)
                .drivers(List.of());
    }

    public static CarCreateEditDto.CarCreateEditDtoBuilder getCarCreateEditDtoBuilder() {
        return CarCreateEditDto.builder()
                .color(DEFAULT_COLOR)
                .brand(DEFAULT_BRAND)
                .number(DEFAULT_NUMBER)
                .year(DEFAULT_YEAR);
    }

    public static DriverReadDto.DriverReadDtoBuilder getDriverReadDtoBuilder() {
        return DriverReadDto.builder()
                .id(DEFAULT_ID)
                .firstName(DEFAUlT_NAME)
                .lastName(DEFAUlT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .gender(Gender.MALE)
                .carId(DEFAULT_ID)
                .rating(DEFAULT_RATING);
    }

    public static DriverCreateEditDto.DriverCreateEditDtoBuilder getDriverCreateEditDtoBuilder() {
        return DriverCreateEditDto.builder()
                .firstName(DEFAUlT_NAME)
                .lastName(DEFAUlT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .gender(Gender.MALE)
                .carId(DEFAULT_ID);
    }

    public static Driver.DriverBuilder getDriverBuilder() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .firstName(DEFAUlT_NAME)
                .lastName(DEFAUlT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .gender(Gender.MALE)
                .car(getCarBuilder().build())
                .rating(DEFAULT_RATING);
    }
}

