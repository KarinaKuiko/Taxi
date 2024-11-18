package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.ride.client.PassengerClient;
import org.example.ride.dto.read.PassengerReadDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PassengerClientService {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passenger-client")
    public PassengerReadDto getPassenger(Long id) {
        return passengerClient.findById(id);
    }
}
