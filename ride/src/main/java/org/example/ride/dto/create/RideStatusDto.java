package org.example.ride.dto.create;

import org.example.ride.entity.enumeration.DriverRideStatus;

public record RideStatusDto(
        DriverRideStatus driverRideStatus
) {
}
