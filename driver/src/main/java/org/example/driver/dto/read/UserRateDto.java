package org.example.driver.dto.read;

public record UserRateDto(
        long userId,
        double averageRate
) {
}
