package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.ride.client.PassengerClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PassengerClientService {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passenger-client")
    public void checkExistingPassenger(Long id) {
        passengerClient.findById(id);
    }
}
