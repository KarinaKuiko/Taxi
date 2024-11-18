package org.example.rating.dto.read;

import java.math.BigDecimal;

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
