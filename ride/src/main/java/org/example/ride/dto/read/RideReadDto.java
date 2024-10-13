package org.example.ride.dto.read;

import org.example.ride.entity.enumeration.RideStatus;

import java.math.BigDecimal;

public record RideReadDto(
        Long id,
        Long driverId,
        Long passengerId,
        String addressFrom,
        String addressTo,
        RideStatus rideStatus,
        BigDecimal cost
) {
}
