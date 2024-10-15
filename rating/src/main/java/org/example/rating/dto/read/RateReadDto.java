package org.example.rating.dto.read;

import org.example.rating.entity.enumeration.UserType;

public record RateReadDto(
        Long id,
        Long rideId,
        String comment,
        Integer rating,
        Long userId,
        UserType userType
) {
}
