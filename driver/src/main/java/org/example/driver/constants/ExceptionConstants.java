package org.example.driver.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionConstants {
    public static final String CAR_NOT_FOUND = "car.not.found";
    public static final String CAR_DUPLICATED_NUMBER = "car.number.duplicated";
    public static final String DRIVER_NOT_FOUND="driver.not.found";
    public static final String DRIVER_DUPLICATED_EMAIL = "driver.email.duplicated";

    public static final String INTERNAL_SERVER_ERROR = "Something went wrong. Please, try again later";

    public static final String CAR_NUMBER_PATTERN = "^[A-Z]{1,2}[0-9]{3}[A-Z]{2,3}$|^[A-Z]{1,2}-[0-9]{3}-[A-Z]{2,3}$|" +
            "^[0-9]{3}-[A-Z]{1,2}-[0-9]{2}$|^[A-Z]{2}-[0-9]{3}-[A-Z]{1,2}$";
    public static final String PHONE_PATTERN = "^(\\+375(29|33|44|25|17|16|15|44|29|33|44)?[0-9]{7}|80(29|33|44|25|17|16|15|44)?[0-9]{7})$";
    public static final String AVATAR_NOT_FOUND = "avatar.not.found";
    public static final String FILE_UPLOAD_EXCEPTION = "file.upload.exception";
    public static final String FILE_DELETE_EXCEPTION = "file.delete.exception";
    public static final String ACCESS_DENIED_EXCEPTION = "access.denied";
}
