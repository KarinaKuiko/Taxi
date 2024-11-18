package org.example.ride.client;

import org.example.ride.dto.read.DriverReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver")
public interface DriverClient {

    @GetMapping("/api/v1/drivers/{id}")
    DriverReadDto findById(@PathVariable("id") Long id);
}
