package org.example.passenger.dto.read;

import java.time.LocalDateTime;

public record ExceptionDto(
        String message,
        LocalDateTime time
) {
}