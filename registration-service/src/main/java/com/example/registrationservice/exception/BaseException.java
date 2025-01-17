package com.example.registrationservice.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }
}
