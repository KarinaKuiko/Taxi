package com.example.exceptionhandlerstarter.keycloak;

import com.example.exceptionhandlerstarter.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public KeycloakException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}
