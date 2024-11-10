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

    public static Car.CarBuilder getCar() {
        return Car.builder()
                .id(DEFAULT_ID)
                .color("red")
                .brand("BMW")
                .number("AB123CD")
                .year(2023)
                .drivers(List.of());
    }

    public static CarReadDto getCarReadDto() {
        return new CarReadDto(DEFAULT_ID, "red", "BMW", "AB123CD", 2023, List.of());

    }

    public static CarCreateEditDto getCarCreateEditDto() {
        return new CarCreateEditDto("red", "BMW", "AB123CD", 2023);
    }

    public static DriverReadDto getDriverReadDto() {
        return new DriverReadDto(DEFAULT_ID, "test",
                "test@gmail.com", "+375297654321", Gender.MALE, 1L, 5.0);
    }

    public static DriverCreateEditDto getDriverCreateEditDto() {
        return new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 1L);
    }

    public static Driver.DriverBuilder getDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .name("test")
                .email("test@gmail.com")
                .phone("+375297654321")
                .gender(Gender.MALE)
                .car(getCar().build())
                .rating(5.0);
    }
}
