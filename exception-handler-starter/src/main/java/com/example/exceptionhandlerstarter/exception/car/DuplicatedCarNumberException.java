package com.example.exceptionhandlerstarter.exception.car;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class DuplicatedCarNumberException extends BaseException {
    public DuplicatedCarNumberException(String message) {
        super(message);
    }
}
