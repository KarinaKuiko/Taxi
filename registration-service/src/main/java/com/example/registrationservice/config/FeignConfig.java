package com.example.registrationservice.config;

import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    JsonFormWriter jsonFormWriter() {
        return new JsonFormWriter();
    }
}
