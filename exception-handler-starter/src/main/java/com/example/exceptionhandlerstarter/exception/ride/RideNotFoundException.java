package com.example.exceptionhandlerstarter.exception.ride;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class RideNotFoundException extends BaseException {
    public RideNotFoundException(String message) {
        super(message);
    }
}
