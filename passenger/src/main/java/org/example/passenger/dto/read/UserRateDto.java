package org.example.passenger.dto.read;

public record UserRateDto(
        Long userId,
        Double averageRate
) {
}
