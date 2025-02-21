package com.example.exceptionhandlerstarter.exception.param;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class InvalidCountParametersException extends BaseException {
    public InvalidCountParametersException(String message) {
        super(message);
    }
}
