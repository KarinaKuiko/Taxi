package org.example.ride.dto.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RideCreateEditDto(

        @Min(value = 1, message = "{id.min}")
        Long driverId,

        @NotNull(message = "{passenger.null}")
        @Min(value = 1, message = "{id.min}")
        Long passengerId,

        @NotBlank(message = "{address.from.blank}")
        String addressFrom,

        @NotBlank(message = "{address.to.blank}")
        String addressTo
) {
}
