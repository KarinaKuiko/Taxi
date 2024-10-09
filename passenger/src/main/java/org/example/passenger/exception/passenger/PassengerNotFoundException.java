package org.example.passenger.exception.passenger;

import org.example.passenger.exception.BaseException;

public class PassengerNotFoundException extends BaseException {
    public PassengerNotFoundException(String message) {
        super(message);
    }
}
