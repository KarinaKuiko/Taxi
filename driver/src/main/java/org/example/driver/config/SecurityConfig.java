package org.example.driver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.example.driver.constants.SecurityConstants.ROLE_ADMIN;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter authConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health",
                                "/driver/swagger-ui/**",
                                "/driver/v3/**",
                                "/driver/swagger-ui.html",
                                "/driver/webjars/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/drivers").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/drivers/list").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/cars").hasRole(ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(authConverter)))
                .csrf(CsrfConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
