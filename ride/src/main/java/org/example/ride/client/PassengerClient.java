package org.example.ride.client;

import org.example.ride.dto.read.PassengerReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger")
public interface PassengerClient {
    @GetMapping("/api/v1/passengers/{id}")
    PassengerReadDto findById(@PathVariable("id") Long id);
}
