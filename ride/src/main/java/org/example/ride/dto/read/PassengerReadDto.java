package org.example.ride.dto.read;

public record PassengerReadDto(
        String name,

        String email,

        String phone,
        Double rating
) {
}
