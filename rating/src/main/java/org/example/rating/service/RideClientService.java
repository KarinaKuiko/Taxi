package org.example.rating.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rating.client.CommonFeignClient;
import org.example.rating.dto.read.RideReadDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RideClientService {
    private final CommonFeignClient rideClient;

    @CircuitBreaker(name = "ride-client")
    public RideReadDto getRide(Long rideId) {
        return rideClient.findById(rideId);
    }
}
