package org.example.rating.e2e.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.rating.RatingApplication;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = RatingApplication.class)
public class CucumberSpringConfiguration {
}
