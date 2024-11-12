package org.example.rating.dto.read;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RideReadDto(
        long id,
        long driverId,
        long passengerId,
        String addressFrom,
        String addressTo,
        String driverRideStatus,
        String passengerRideStatus,
        BigDecimal cost
) {
}
