package com.example.registrationservice.client;

import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.PassengerReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "passenger")
public interface PassengerClient {

    @PostMapping("/api/v1/passengers")
    PassengerReadDto createPassenger(@RequestBody SignUpDto signUpDto,
                                     @RequestHeader("Authorization") String authorization);
}
