package com.example.exceptionhandlerstarter.config;

import com.example.exceptionhandlerstarter.exception.BasicExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {

    @Bean
    @ConditionalOnMissingBean(BasicExceptionHandler.class)
    public BasicExceptionHandler basicExceptionHandler() {
        return new BasicExceptionHandler();
    }
}
