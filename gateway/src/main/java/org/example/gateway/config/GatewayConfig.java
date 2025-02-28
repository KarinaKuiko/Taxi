package org.example.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

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
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/driver/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://driver"))
                .route(r -> r.path("/passenger/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://passenger"))
                .route(r -> r.path("/rating/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://rating"))
                .route(r -> r.path("/ride/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://ride"))
                .route(r -> r.path("/registration-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://registration-service"))
                .build();
    }
}
