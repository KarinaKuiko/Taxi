package org.example.driver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Taxi",
                description = "Taxi aggregator", version = "1.0.0",
                contact = @Contact(
                        name = "Karina Kuiko",
                        email = "KarinaIhorevna@gmail.com"
                )
        )
)
public class OpenApiConfig {

}