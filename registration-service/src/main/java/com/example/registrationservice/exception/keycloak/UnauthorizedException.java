package com.example.registrationservice.exception.keycloak;

import com.example.registrationservice.exception.BaseException;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

