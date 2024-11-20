package org.example.driver.e2e.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.driver.DriverApplication;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = DriverApplication.class)
public class CucumberSpringConfiguration {
}
