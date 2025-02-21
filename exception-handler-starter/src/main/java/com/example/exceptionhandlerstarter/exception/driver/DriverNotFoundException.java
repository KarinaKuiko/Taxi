package com.example.exceptionhandlerstarter.exception.driver;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class DriverNotFoundException extends BaseException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}
