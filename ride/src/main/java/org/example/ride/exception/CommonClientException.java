package org.example.ride.exception;

import org.example.ride.dto.read.ExceptionDto;

public class CommonClientException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public CommonClientException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}
