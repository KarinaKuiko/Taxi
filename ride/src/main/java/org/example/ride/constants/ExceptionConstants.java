package org.example.ride.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionConstants {
    public static final String RIDE_NOT_FOUND_EXCEPTION_MESSAGE = "ride.not.found";
    public static final String INVALID_PROPOSED_STATUS_MESSAGE = "status.proposed.invalid";
    public static final String CANCELED_STATUS_MESSAGE = "status.canceled";
    public static final String INVALID_COUNT_PARAMETERS_MESSAGE = "param.count.invalid";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong. Please, try again later";

}
