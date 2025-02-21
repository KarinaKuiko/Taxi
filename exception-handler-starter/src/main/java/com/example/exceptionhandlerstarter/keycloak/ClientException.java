package com.example.exceptionhandlerstarter.keycloak;

import com.example.exceptionhandlerstarter.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class ClientException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public ClientException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}