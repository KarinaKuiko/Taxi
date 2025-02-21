package com.example.exceptionhandlerstarter.exception.ride;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class InvalidRideStatusForChangingException extends BaseException {
    public InvalidRideStatusForChangingException(String message) {
        super(message);
    }
}
