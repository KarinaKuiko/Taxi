package org.example.driver.dto.read;

import org.example.driver.entity.enumeration.DriverRideStatus;
import org.example.driver.entity.enumeration.PassengerRideStatus;

import java.math.BigDecimal;

public record RideReadDto(
        Long id,
        Long driverId,
        Long passengerId,
        String addressFrom,
        String addressTo,
        DriverRideStatus driverRideStatus,
        PassengerRideStatus passengerRideStatus,
        BigDecimal cost
) {
}
