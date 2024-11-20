package org.example.ride.client;

import org.example.ride.dto.read.PassengerReadDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CommonPassengerClient {
    @GetMapping("/api/v1/passengers/{id}")
    PassengerReadDto findById(@PathVariable("id") Long id);
}
