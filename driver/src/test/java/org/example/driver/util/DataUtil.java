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
    public static final String AUTH_URL = "http://localhost:8085/api/v1/taxi/sign-in";

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
    public static final String DEFAULT_NAME = "test";
    public static final String DEFAULT_EMAIL = "test@gmail.com";
    public static final String DEFAULT_PHONE = "+375297654321";
    public static final double DEFAULT_RATING = 5.0;
    public static final String DEFAULT_PASSWORD = "password";

    public static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJSX1IzeDRpNDBuaS0zSXdWVE1kcWx3QThLZTU2VXlUeWxNVjVzMXhpVUtJIn0.eyJleHAiOjE3Mzg4NjY0NDQsImlhdCI6MTczODg2NjE0NCwianRpIjoiY2NlMzc0YzEtNzI1Ny00MjE2LTlhZTUtMjY0YjlkZGRhOGZkIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjg0ODQvcmVhbG1zL3RheGlfcmVhbG0iLCJhdWQiOlsicmVhbG0tbWFuYWdlbWVudCIsImFjY291bnQiXSwic3ViIjoiNTBjNjE4MWEtYWQ3OS00MjY3LTk0NTItN2Q5MDZiZmViZTYyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidGF4aV9jbGllbnQiLCJzaWQiOiJmNTAyOGY2Yy00MGJmLTQyMmItYjc1Ny1hNTM1NDM0M2MzOTEiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsIkRSSVZFUiIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy10YXhpX3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJtYW5hZ2UtdXNlcnMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJnZW5kZXIiOiJNQUxFIiwibmFtZSI6ImRyaXZlciBkcml2ZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJkcml2ZXIxMUBnbWFpbC5jb20iLCJnaXZlbl9uYW1lIjoiZHJpdmVyIiwiZmFtaWx5X25hbWUiOiJkcml2ZXIiLCJlbWFpbCI6ImRyaXZlcjExQGdtYWlsLmNvbSJ9.MAw8LevVt_IVrtJNBg95RwpIQ6D--NWW_ht3gzxrCJjaOdZJt-M8-flQn-twaO2ezk7kIIYxgxLzDcqRMFHF89zXEQzjdeRIAMc2jWWSi_On1v3t2bC4O_A9fcbEN869hNEQxC3ec1XJCaqtJcIri6tskPSpbaGEoT1uoa0jtbVde5YgYsTTolTprsuYymkfaIsp7pbvJuhr7AcHyRAZJVBoU-Npkwn-lNBDxs_5jbC_rCMv31lLV7ukKekGOcMYzc9CtO_4-APlfPhnsdaw6eQQMFaPwysB0mzrBD8fAHlmUxyY0CJDO1QaUBrfcaTs0s5NilH3IIecKayc5HbhSA";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

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
                .firstName(DEFAULT_NAME)
                .lastName(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .gender(Gender.MALE)
                .carId(DEFAULT_ID)
                .rating(DEFAULT_RATING);
    }

    public static DriverCreateEditDto.DriverCreateEditDtoBuilder getDriverCreateEditDtoBuilder() {
        return DriverCreateEditDto.builder()
                .firstName(DEFAULT_NAME)
                .lastName(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .gender(Gender.MALE)
                .carCreateEditDto(getCarCreateEditDtoBuilder().build());
    }

    public static Driver.DriverBuilder getDriverBuilder() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .firstName(DEFAULT_NAME)
                .lastName(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .gender(Gender.MALE)
                .car(getCarBuilder().build())
                .rating(DEFAULT_RATING);
    }

    public static SignInUserDto signInUserDto() {
        return new SignInUserDto(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }
}

