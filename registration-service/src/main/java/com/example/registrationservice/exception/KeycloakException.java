package com.example.registrationservice.exception;

import com.example.registrationservice.dto.read.ExceptionDto;
import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public KeycloakException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}
