package com.example.exceptionhandlerstarter.exception.passenger;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class PassengerNotFoundException extends BaseException {
    public PassengerNotFoundException(String message) {
        super(message);
    }
}
