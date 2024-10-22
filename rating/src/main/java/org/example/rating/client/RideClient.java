package org.example.rating.client;

import org.example.rating.dto.read.RideReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ride", url = "http://localhost:8083/api/v1/rides")
public interface RideClient {

    @GetMapping("/{id}")
    RideReadDto findById(@PathVariable("id") Long id);
}
