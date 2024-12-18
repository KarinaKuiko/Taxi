package org.example.driver.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.example.driver.e2e.config.CucumberSpringConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features/driver.feature"},
        glue={"org.example.driver.e2e"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/report.html",
                "json:target/cucumber-reports/report.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        })
@ContextConfiguration(classes = {CucumberSpringConfiguration.class})
@SpringBootTest
public class CucumberRunnerTest {
}

