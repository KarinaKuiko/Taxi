package org.example.driver.dto.read;

public record UserRateDto(
        Long userId,
        Double averageRate
) {
}
