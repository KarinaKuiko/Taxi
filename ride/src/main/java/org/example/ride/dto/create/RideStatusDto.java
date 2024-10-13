package org.example.ride.dto.create;

import org.example.ride.entity.enumeration.RideStatus;

public record RideStatusDto(
        RideStatus rideStatus
) {
}
