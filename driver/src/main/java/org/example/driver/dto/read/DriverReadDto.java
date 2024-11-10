package org.example.driver.dto.read;

import org.example.driver.entity.enumeration.Gender;

public record DriverReadDto(
        long id,
        String name,
        String email,
        String phone,
        Gender gender,
        Long carId,
        double rating
) {
}
