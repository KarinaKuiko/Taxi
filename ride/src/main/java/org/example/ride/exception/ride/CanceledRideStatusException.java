package org.example.ride.exception.ride;

import org.example.ride.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CanceledRideStatusException extends BaseException {
    public CanceledRideStatusException(String message, HttpStatus status) {
        super(message, status);
    }
}
