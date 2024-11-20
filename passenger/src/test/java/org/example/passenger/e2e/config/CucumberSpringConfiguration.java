package org.example.passenger.e2e.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.passenger.PassengerApplication;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = PassengerApplication.class)
public class CucumberSpringConfiguration {
}
