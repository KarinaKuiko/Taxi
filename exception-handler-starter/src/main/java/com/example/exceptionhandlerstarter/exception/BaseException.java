package com.example.exceptionhandlerstarter.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }
}