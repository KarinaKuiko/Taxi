package org.example.driver.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features/driver.feature"},
        glue={"org.example.driver.e2e"})
public class CucumberRunnerTest {
}
