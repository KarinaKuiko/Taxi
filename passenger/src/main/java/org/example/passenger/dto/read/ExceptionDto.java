package org.example.passenger.dto.read;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ExceptionDto(
        HttpStatus status,
        String message,
        LocalDateTime time
) {
}