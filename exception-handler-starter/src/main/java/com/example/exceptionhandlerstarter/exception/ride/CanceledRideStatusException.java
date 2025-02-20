package com.example.exceptionhandlerstarter.exception.ride;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class CanceledRideStatusException extends BaseException {
    public CanceledRideStatusException(String message) {
        super(message);
    }
}
