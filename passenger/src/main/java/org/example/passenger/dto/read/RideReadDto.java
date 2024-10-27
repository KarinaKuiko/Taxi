package org.example.passenger.dto.read;

import org.example.passenger.entity.enumeration.DriverRideStatus;
import org.example.passenger.entity.enumeration.PassengerRideStatus;

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
