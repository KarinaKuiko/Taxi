package org.example.rating.dto.read;

import java.math.BigDecimal;

public record RideReadDto(
        Long id,
        Long driverId,
        Long passengerId,
        String addressFrom,
        String addressTo,
        String driverRideStatus,
        String passengerRideStatus,
        BigDecimal cost
) {
}
