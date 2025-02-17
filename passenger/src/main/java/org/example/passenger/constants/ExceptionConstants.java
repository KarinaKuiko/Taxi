package org.example.passenger.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionConstants {
    public static final String PASSENGER_NOT_FOUND_MESSAGE = "passenger.not.found";
    public static final String PASSENGER_DUPlICATED_EMAIL_MESSAGE = "passenger.email.duplicated";

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong. Please, try again later";

    public static final String PHONE_PATTERN = "^(\\+375(29|33|44|25|17|16|15|44|29|33|44)?[0-9]{7}|80(29|33|44|25|17|16|15|44)?[0-9]{7})$";
    public static final String AVATAR_NOT_FOUND = "avatar.not.found";
}
