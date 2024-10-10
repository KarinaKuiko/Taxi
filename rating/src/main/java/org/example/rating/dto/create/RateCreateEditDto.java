package org.example.rating.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateCreateEditDto(
        @NotNull(message = "{ride.null}")
        Long rideId,
        String comment,

        @Min(value = 1, message = "{rate.range}")
        @Max(value = 5, message = "{rate.range}")
        Integer rate
) {
}
