package org.example.passenger.dto.read;

public record UserRateDto(
        long userId,
        double averageRate
) {
}
