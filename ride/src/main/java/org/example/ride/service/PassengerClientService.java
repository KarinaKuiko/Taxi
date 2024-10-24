package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ride.client.PassengerClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PassengerClientService {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passenger-client", fallbackMethod = "fallbackMethod")
    public void checkExistingPassenger(Long id) {
        passengerClient.findById(id);
    }

    private void fallbackMethod(RuntimeException e) throws RuntimeException {
        log.info(e.getMessage());
        throw e;
    }
}
