package org.example.rating.dto.read;

public record RideReadDto(
        long id,
        long driverId,
        long passengerId,
        String addressFrom,
        String addressTo,
        String driverRideStatus,
        String passengerRideStatus,
        double cost
) {
}
