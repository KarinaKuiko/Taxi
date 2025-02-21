package com.example.registrationservice.client;

import com.example.registrationservice.config.FeignConfig;
import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.DriverReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "driver", configuration = FeignConfig.class)
public interface DriverClient {

    @PostMapping(value = "/api/v1/drivers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    DriverReadDto createDriver(@RequestPart SignUpDto dto,
                               @RequestPart(required = false) MultipartFile file,
                               @RequestHeader("Authorization") String authorization);
}
