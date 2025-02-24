package org.example.driver.dto.read;

import lombok.Builder;
import org.example.driver.entity.enumeration.Gender;

import java.io.Serializable;

@Builder
public record DriverReadDto(
        long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Gender gender,
        Long carId,
        double rating,
        String imageUrl
) implements Serializable {
}
