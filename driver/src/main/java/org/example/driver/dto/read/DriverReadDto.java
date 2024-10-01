package org.example.driver.dto.read;

import org.example.driver.entity.enumeration.Gender;

import java.util.UUID;

public record DriverReadDto(
        Long id,
        String name,
        String email,
        String phone,
        Gender gender,
        UUID car_id
) {
}
