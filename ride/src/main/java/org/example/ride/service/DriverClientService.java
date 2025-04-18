package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.ride.client.CommonDriverClient;
import org.example.ride.dto.read.DriverReadDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DriverClientService {
    private final CommonDriverClient driverClient;

    @CircuitBreaker(name = "driver-client")
    public DriverReadDto getDriver(Long id) {
        return driverClient.findById(id);
    }
}
