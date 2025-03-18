package com.example.reportservice.client;

import com.example.reportservice.dto.DriverReadDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "driver")
public interface DriverClient {
    @GetMapping("/api/v1/drivers/{id}")
    @CircuitBreaker(name = "driver-client")
    DriverReadDto findById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/drivers/list")
    @CircuitBreaker(name = "driver-client")
    List<DriverReadDto> findFullList();
}
