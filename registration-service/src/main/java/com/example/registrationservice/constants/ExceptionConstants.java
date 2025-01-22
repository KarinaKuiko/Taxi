package com.example.registrationservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionConstants {
    public static final String KEYCLOAK_CREATE_USER_EXCEPTION_MESSAGE = "create.user.exception";
    public static final String SERVICE_UNAVAILABLE_MESSAGE = "service.unavailable";
    public static final String UNAUTHORIZED_MESSAGE = "unauthorized";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong. Please, try again later";

}
