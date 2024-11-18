package org.example.driver.dto.read;

import lombok.Builder;

import java.util.List;

@Builder
public record CarReadDto(
        long id,
        String color,
        String brand,
        String number,
        int year,
        List<DriverReadDto> drivers
) {
}
