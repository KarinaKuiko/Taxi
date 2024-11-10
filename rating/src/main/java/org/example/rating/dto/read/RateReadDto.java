package org.example.rating.dto.read;

import org.example.rating.entity.enumeration.UserType;

public record RateReadDto(
        long id,
        long rideId,
        String comment,
        Integer rating,
        long userId,
        UserType userType
) {
}
