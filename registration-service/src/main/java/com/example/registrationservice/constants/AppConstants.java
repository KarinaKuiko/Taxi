package com.example.registrationservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConstants {
    public static final String PHONE_PATTERN = "^(\\+375(29|33|44|25|17|16|15|44|29|33|44)?[0-9]{7}|80(29|33|44|25|17|16|15|44)?[0-9]{7})$";
    public static final String BEARER_PREFIX = "Bearer ";
}
