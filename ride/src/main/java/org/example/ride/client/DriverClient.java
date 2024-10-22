package org.example.ride.client;

import org.example.ride.dto.read.DriverReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver", url = "http://localhost:8081/api/v1/drivers")
public interface DriverClient {

    @GetMapping("/{id}")
    DriverReadDto findById(@PathVariable("id") Long id);
}
