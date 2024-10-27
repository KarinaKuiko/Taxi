package org.example.ride.dto.create;

import org.example.ride.entity.enumeration.PassengerRideStatus;

public record PassengerRideStatusDto(
        PassengerRideStatus rideStatus
) {
}
