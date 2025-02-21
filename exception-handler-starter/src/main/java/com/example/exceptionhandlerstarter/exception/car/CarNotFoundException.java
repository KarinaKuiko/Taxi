package com.example.exceptionhandlerstarter.exception.car;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class CarNotFoundException extends BaseException {

    public CarNotFoundException(String message) {
        super(message);
    }
}
