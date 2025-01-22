package org.example.passenger.dto.read;

import lombok.Builder;

@Builder
public record PassengerReadDto(
        long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        double rating,
        String gender
) {
}
