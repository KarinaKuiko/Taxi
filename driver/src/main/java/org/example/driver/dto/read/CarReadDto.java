package org.example.driver.dto.read;

import java.util.List;

public record CarReadDto(
        Long id,
        String color,
        String brand,
        String number,
        Integer year,
        List<DriverReadDto> drivers
) {
}
