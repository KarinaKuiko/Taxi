package org.example.ride.dto.read;

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
