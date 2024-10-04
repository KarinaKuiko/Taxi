package org.example.driver.dto.read;

import org.example.driver.entity.enumeration.Gender;

public record DriverReadDto(
        Long id,
        String name,
        String email,
        String phone,
        Gender gender,
        Long carId
) {
}
