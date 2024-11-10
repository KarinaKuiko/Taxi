package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ride.client.DriverClient;
import org.example.ride.dto.read.DriverReadDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class DriverClientService {
    private final DriverClient driverClient;

    @CircuitBreaker(name = "driver-client")
    public DriverReadDto getDriver(Long id) {
        return driverClient.findById(id);
    }
}
