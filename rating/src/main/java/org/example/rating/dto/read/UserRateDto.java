package org.example.rating.dto.read;

public record UserRateDto(
        Long userId,
        Double averageRate
) {
}
