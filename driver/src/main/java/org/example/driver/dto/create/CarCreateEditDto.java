package org.example.driver.dto.create;

public record CarCreateEditDto(
        String color,
        String brand,
        String number,
        Integer year
) {
}
