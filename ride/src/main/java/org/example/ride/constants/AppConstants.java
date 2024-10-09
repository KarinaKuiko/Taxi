package org.example.ride.constants;

import java.math.BigDecimal;

public class AppConstants {

    //Exception message
    public static final String RIDE_NOT_FOUND_EXCEPTION = "ride.not.found";
    public static final String INVALID_PROPOSED_STATUS = "status.proposed.invalid";
    public static final String CANCELED_STATUS = "status.canceled";
    public static final String INVALID_COUNT_PARAMETERS = "param.count.invalid";
    public static final String INTERNAL_SERVER_ERROR="Something went wrong. Please, try again later";

    //PriceGenerator
    public static final int SCALE = 2;
    public static final BigDecimal MAX_VALUE = new BigDecimal("9999.99");

}
