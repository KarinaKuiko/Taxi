package org.example.driver.dto.read;

import org.example.driver.entity.Driver;

import java.util.List;
import java.util.UUID;

public record CarReadDto(
        Long id,
        String color,
        String brand,
        String number,
        Integer year,
        List<DriverReadDto> drivers
) {
}
