package org.example.rating.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rating.client.RideClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RideClientService {
    private final RideClient rideClient;

    @CircuitBreaker(name = "ride-client", fallbackMethod = "fallbackMethod")
    public void checkExistingRide(Long rideId) {
        rideClient.findById(rideId);
    }

    private void fallbackMethod(RuntimeException e) throws RuntimeException {
        log.info(e.getMessage());
        throw e;
    }
}
