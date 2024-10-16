package org.example.rating.exception.ride;

import lombok.Getter;
import org.example.rating.dto.read.ExceptionDto;

@Getter
public class RideException extends RuntimeException {
    private final ExceptionDto exceptionDto;

    public RideException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }
}
