package com.example.registrationservice.client;

import com.example.registrationservice.config.FeignConfig;
import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.PassengerReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "passenger", configuration = FeignConfig.class)
public interface PassengerClient {

    @PostMapping(value = "/api/v1/passengers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PassengerReadDto createPassenger(@RequestPart SignUpDto dto,
                                     @RequestPart(required = false) MultipartFile file,
                                     @RequestHeader("Authorization") String authorization);
}
