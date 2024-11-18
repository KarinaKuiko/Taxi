package org.example.rating.dto.read;

import lombok.Builder;
import org.example.rating.entity.enumeration.UserType;

@Builder
public record RateReadDto(
        long id,
        long rideId,
        String comment,
        Integer rating,
        long userId,
        UserType userType
) {
}
