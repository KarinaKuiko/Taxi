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
    public static final String PASSENGER_DUPLICATED_EMAIL_MESSAGE = "Passenger with this email already exists";


    //Passenger
    public static final String DEFAULT_NAME = "passenger";
    public static final String DEFAULT_EMAIL = "passenger@gmail.com";
    public static final String DEFAULT_PHONE = "+375441234567";
    public static final double DEFAULT_RATING = 5.0;

    public static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJSX1IzeDRpNDBuaS0zSXdWVE1kcWx3QThLZTU2VXlUeWxNVjVzMXhpVUtJIn0.eyJleHAiOjE3Mzg4NjY0NDQsImlhdCI6MTczODg2NjE0NCwianRpIjoiY2NlMzc0YzEtNzI1Ny00MjE2LTlhZTUtMjY0YjlkZGRhOGZkIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjg0ODQvcmVhbG1zL3RheGlfcmVhbG0iLCJhdWQiOlsicmVhbG0tbWFuYWdlbWVudCIsImFjY291bnQiXSwic3ViIjoiNTBjNjE4MWEtYWQ3OS00MjY3LTk0NTItN2Q5MDZiZmViZTYyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidGF4aV9jbGllbnQiLCJzaWQiOiJmNTAyOGY2Yy00MGJmLTQyMmItYjc1Ny1hNTM1NDM0M2MzOTEiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsIkRSSVZFUiIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy10YXhpX3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJtYW5hZ2UtdXNlcnMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJnZW5kZXIiOiJNQUxFIiwibmFtZSI6ImRyaXZlciBkcml2ZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJkcml2ZXIxMUBnbWFpbC5jb20iLCJnaXZlbl9uYW1lIjoiZHJpdmVyIiwiZmFtaWx5X25hbWUiOiJkcml2ZXIiLCJlbWFpbCI6ImRyaXZlcjExQGdtYWlsLmNvbSJ9.MAw8LevVt_IVrtJNBg95RwpIQ6D--NWW_ht3gzxrCJjaOdZJt-M8-flQn-twaO2ezk7kIIYxgxLzDcqRMFHF89zXEQzjdeRIAMc2jWWSi_On1v3t2bC4O_A9fcbEN869hNEQxC3ec1XJCaqtJcIri6tskPSpbaGEoT1uoa0jtbVde5YgYsTTolTprsuYymkfaIsp7pbvJuhr7AcHyRAZJVBoU-Npkwn-lNBDxs_5jbC_rCMv31lLV7ukKekGOcMYzc9CtO_4-APlfPhnsdaw6eQQMFaPwysB0mzrBD8fAHlmUxyY0CJDO1QaUBrfcaTs0s5NilH3IIecKayc5HbhSA";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";


    public static PassengerReadDto.PassengerReadDtoBuilder getPassengerReadDtoBuilder() {
        return PassengerReadDto.builder()
                .id(DEFAULT_ID)
                .firstName(DEFAULT_NAME)
                .lastName(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING);
    }

    public static PassengerCreateEditDto.PassengerCreateEditDtoBuilder getPassengerCreateEditDtoBuilder() {
        return PassengerCreateEditDto.builder()
                .firstName(DEFAULT_NAME)
                .lastName(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE);
    }

    public static Passenger.PassengerBuilder getPassengerBuilder () {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .firstName(DEFAULT_NAME)
                .lastName(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING);
    }
}
