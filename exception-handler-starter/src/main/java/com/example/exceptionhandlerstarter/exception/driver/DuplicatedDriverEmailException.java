package com.example.exceptionhandlerstarter.exception.driver;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class DuplicatedDriverEmailException extends BaseException {
    public DuplicatedDriverEmailException(String message) {
        super(message);
    }
}
