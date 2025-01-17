package com.example.registrationservice.client;

import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.DriverReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "driver")
public interface DriverClient {

    @PostMapping("/api/v1/drivers")
    DriverReadDto createDriver(@RequestBody SignUpDto signUpDto,
                               @RequestHeader("Authorization") String authorization);
}
