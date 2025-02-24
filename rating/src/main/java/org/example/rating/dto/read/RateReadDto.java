package org.example.rating.dto.read;

import lombok.Builder;
import org.example.rating.entity.enumeration.UserType;

import java.io.Serializable;

@Builder
public record RateReadDto(
        long id,
        long rideId,
        String comment,
        int rating,
        Long userId,
        UserType userType
) implements Serializable {
}
