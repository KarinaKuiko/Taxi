package org.example.ride.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.ride.entity.enumeration.RideStatus;

public record RideCreateEditDto(
        Long driverId,

        @NotNull(message = "{passenger.null}")
        Long passengerId,

        @NotBlank(message = "{address.from.blank}")
        String addressFrom,

        @NotBlank(message = "{address.from.blank}")
        String addressTo,

        RideStatus rideStatus,

        @NotNull(message = "{cost.blank}")
        Long cost
) {
}
