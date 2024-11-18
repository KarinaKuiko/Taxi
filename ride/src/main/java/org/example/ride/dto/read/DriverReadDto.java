package org.example.ride.dto.read;

public record DriverReadDto(
        Long id,
        String name,
        String email,
        String phone,
        String gender,
        Long carId,
        Double rating
) {
}
