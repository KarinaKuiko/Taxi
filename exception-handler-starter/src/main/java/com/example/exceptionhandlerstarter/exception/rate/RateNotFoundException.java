package com.example.exceptionhandlerstarter.exception.rate;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class RateNotFoundException extends BaseException {
    public RateNotFoundException(String message) {
        super(message);
    }
}
