package com.example.exceptionhandlerstarter.keycloak;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

