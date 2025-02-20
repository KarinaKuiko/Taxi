package com.example.exceptionhandlerstarter.exception.ride;

import com.example.exceptionhandlerstarter.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class RideException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public RideException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}
