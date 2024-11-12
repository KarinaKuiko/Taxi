package org.example.passenger.dto.read;

import lombok.Builder;

@Builder
public record PassengerReadDto(
        long id,
        String name,

        String email,

        String phone,
        double rating
) {
}
