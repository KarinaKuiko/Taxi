package org.example.passenger.dto.read;

public record PassengerReadDto(
        Long id,
        String name,

        String email,

        String phone,
        Double rating
) {
}
