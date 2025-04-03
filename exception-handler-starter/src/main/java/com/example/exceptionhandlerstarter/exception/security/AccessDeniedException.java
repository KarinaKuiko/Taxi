package com.example.exceptionhandlerstarter.exception.security;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
