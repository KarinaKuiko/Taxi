package org.example.passenger.dto.read;

public record PassengerReadDto(
        long id,
        String name,

        String email,

        String phone,
        double rating
) {
}
