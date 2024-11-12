package org.example.driver.util;

import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.entity.enumeration.Gender;

import java.util.List;

public class DataUtil {
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final Integer PAGE_VALUE = 0;
    public static final Integer LIMIT_VALUE = 10;
    public static final String URL = "/api/v1/{entity}";
    public static final String URL_WITH_ID = URL + "/{id}";
    public static final Long DEFAULT_ID = 1L;
    public static final String CAR_ENTITY = "cars";
    public static final String DRIVER_ENTITY = "drivers";

    public static Car.CarBuilder getCarBuilder() {
        return Car.builder()
                .id(DEFAULT_ID)
                .color("red")
                .brand("BMW")
                .number("AB123CD")
                .year(2023)
                .drivers(List.of());
    }

    public static CarReadDto.CarReadDtoBuilder getCarReadDtoBuilder() {
        return CarReadDto.builder()
                .id(DEFAULT_ID)
                .color("red")
                .brand("BMW")
                .number("AB123CD")
                .year(2023)
                .drivers(List.of());
    }

    public static CarCreateEditDto.CarCreateEditDtoBuilder getCarCreateEditDtoBuilder() {
        return CarCreateEditDto.builder()
                .color("red")
                .brand("BMW")
                .number("AB123CD")
                .year(2023);
    }

    public static DriverReadDto.DriverReadDtoBuilder getDriverReadDtoBuilder() {
        return DriverReadDto.builder()
                .id(DEFAULT_ID)
                .name("test")
                .email("test@gmail.com")
                .phone("+375297654321")
                .gender(Gender.MALE)
                .carId(DEFAULT_ID)
                .rating(5.0);
    }

    public static DriverCreateEditDto.DriverCreateEditDtoBuilder getDriverCreateEditDtoBuilder() {
        return DriverCreateEditDto.builder()
                .name("test")
                .email("test@gmail.com")
                .phone("+375297654321")
                .gender(Gender.MALE)
                .carId(DEFAULT_ID);
    }

    public static Driver.DriverBuilder getDriverBuilder() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .name("test")
                .email("test@gmail.com")
                .phone("+375297654321")
                .gender(Gender.MALE)
                .car(getCarBuilder().build())
                .rating(5.0);
    }
}
