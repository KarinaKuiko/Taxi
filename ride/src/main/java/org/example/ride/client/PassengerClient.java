package org.example.ride.client;

import org.example.ride.dto.read.PassengerReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger", url = "http://localhost:8082/api/v1/passengers")
public interface PassengerClient {
    @GetMapping("/{id}")
    public PassengerReadDto findById(@PathVariable("id") Long id);
}
