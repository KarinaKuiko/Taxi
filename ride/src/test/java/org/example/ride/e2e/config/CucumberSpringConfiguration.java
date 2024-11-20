package org.example.ride.e2e.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.ride.RideApplication;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = RideApplication.class)
public class CucumberSpringConfiguration {
}