package org.example.rating.dto.read;

import org.example.rating.entity.enumeration.UserType;

public record RateReadDto(
        Long rideId,
        String comment,
        Integer rate,
        Long userId,
        UserType userType
) {
}
