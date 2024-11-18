package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.ride.client.DriverClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DriverClientService {
    private final DriverClient driverClient;

    @CircuitBreaker(name = "driver-client")
    public void checkExistingDriver(Long id) {
        driverClient.findById(id);
    }
}
