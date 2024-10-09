package org.example.ride.exception.param;

import org.example.ride.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCountParametersException extends BaseException {
    public InvalidCountParametersException(String message, HttpStatus status) {
        super(message, status);
    }
}
