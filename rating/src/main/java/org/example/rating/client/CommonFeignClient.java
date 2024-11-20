package org.example.rating.client;

import org.example.rating.dto.read.RideReadDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CommonFeignClient {
    @GetMapping("/api/v1/rides/{id}")
    RideReadDto findById(@PathVariable("id") Long id);
}
