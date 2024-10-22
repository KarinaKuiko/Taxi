package org.example.ride.exception;

import lombok.Getter;
import org.example.ride.dto.read.ExceptionDto;

@Getter
public class CommonClientException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public CommonClientException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}
