package com.example.registrationservice.exception;

import com.example.registrationservice.dto.read.ExceptionDto;
import lombok.Getter;

@Getter
public class ClientException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public ClientException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}