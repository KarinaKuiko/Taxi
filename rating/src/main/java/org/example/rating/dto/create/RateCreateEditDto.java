package org.example.rating.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.example.rating.entity.enumeration.UserType;

@Builder
public record RateCreateEditDto(
        @NotNull(message = "{ride.null}")
        Long rideId,
        String comment,

        @Min(value = 1, message = "{rating.range}")
        @Max(value = 5, message = "{rating.range}")
        Integer rating,
        Long userId,
        UserType userType
) {
}
