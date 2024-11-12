package org.example.ride.dto.read;

import lombok.Builder;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;

import java.math.BigDecimal;

@Builder
public record RideReadDto(
        long id,
        Long driverId,
        long passengerId,
        String addressFrom,
        String addressTo,
        DriverRideStatus driverRideStatus,
        PassengerRideStatus passengerRideStatus,
        BigDecimal cost
) {
}
