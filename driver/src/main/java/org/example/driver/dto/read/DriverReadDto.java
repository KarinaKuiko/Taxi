package org.example.driver.dto.read;

import lombok.Builder;
import org.example.driver.entity.enumeration.Gender;

@Builder
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
