package org.example.ride.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ride.client.PassengerClient;
import org.example.ride.dto.read.PassengerReadDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PassengerClientService {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passenger-client")
    public PassengerReadDto checkExistingPassenger(Long id) {
        return passengerClient.findById(id);
    }
}
