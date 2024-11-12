package org.example.ride.dto.read;

import lombok.Builder;

@Builder
public record DriverReadDto(
        long id,
        String name,
        String email,
        String phone,
        String gender,
        Long carId,
        double rating
) {
}
