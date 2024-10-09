package org.example.ride.exception.ride;

import org.example.ride.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RideNotFoundException extends BaseException {
    public RideNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
