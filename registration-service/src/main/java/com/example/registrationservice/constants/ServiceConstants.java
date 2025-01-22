package com.example.registrationservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceConstants {
    public static final String GRANT_TYPE_FIELD = "grant_type";
    public static final String CLIENT_ID_FIELD = "client_id";
    public static final String CLIENT_SECRET_FIELD = "client_secret";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String GENDER_FIELD = "gender";
    public static final String PHONE_FIELD = "phone";

    public static final String GRANT_TYPE_PASSWORD_FIELD = "password";
    public static final String GRANT_TYPE_CLIENT_CREDENTIAlS_FIELD = "client_credentials";
    public static final String PASSENGER_ROLE = "PASSENGER";
    public static final String DRIVER_ROLE = "DRIVER";
}
