package org.example.driver.dto.create;

import org.example.driver.entity.enumeration.Gender;

import java.util.UUID;

public record DriverCreateEditDto(
        String name,
        String email,
        String phone,
        Gender gender,
        Long car_id
) {
}
