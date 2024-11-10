package org.example.ride.dto.read;

public record PassengerReadDto(
        long id,
        String name,

        String email,

        String phone,
        double rating
) {
}
