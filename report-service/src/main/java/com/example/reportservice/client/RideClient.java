package com.example.reportservice.client;


import com.example.reportservice.dto.RideReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ride")
public interface RideClient {
    @GetMapping("/api/v1/rides/driver-list")
    List<RideReadDto> findFullListByDriverId(@RequestParam(name = "driverId") Long driverId);
}
