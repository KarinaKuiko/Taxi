package com.example.reportservice.client;


import com.example.reportservice.dto.RideReadDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ride")
public interface RideClient {
    @GetMapping("/api/v1/rides/driver-list")
    @CircuitBreaker(name = "ride-client")
    List<RideReadDto> findTop100ByDriverId(@RequestParam(name = "driverId") Long driverId);
}
