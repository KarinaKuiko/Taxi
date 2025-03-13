package com.example.reportservice.dto;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record RideReadDto(
        long id,
        Long driverId,
        long passengerId,
        String addressFrom,
        String addressTo,
        String driverRideStatus,
        String passengerRideStatus,
        BigDecimal cost
) implements Serializable {
}
