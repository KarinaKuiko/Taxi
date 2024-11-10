package org.example.driver.dto.read;

import java.util.List;

public record CarReadDto(
        long id,
        String color,
        String brand,
        String number,
        int year,
        List<DriverReadDto> drivers
) {
}
