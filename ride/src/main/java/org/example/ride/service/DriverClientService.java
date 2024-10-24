package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ride.client.DriverClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class DriverClientService {
    private final DriverClient driverClient;

    @CircuitBreaker(name = "ride", fallbackMethod = "fallbackMethod")
    public void checkExistingDriver(Long id) {
        driverClient.findById(id);
    }

    private void fallbackMethod(RuntimeException e) throws RuntimeException {
        log.info(e.getMessage());
        throw e;
    }
}
