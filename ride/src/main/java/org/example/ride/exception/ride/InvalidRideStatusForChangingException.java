package org.example.ride.exception.ride;

import org.example.ride.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRideStatusForChangingException extends BaseException {
    public InvalidRideStatusForChangingException(String message, HttpStatus status) {
        super(message, status);
    }
}
