package org.example.passenger.exception.passenger;

import org.example.passenger.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PassengerNotFoundException extends BaseException {
    public PassengerNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
