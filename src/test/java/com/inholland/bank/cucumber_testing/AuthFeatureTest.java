package com.inholland.bank.cucumber_testing;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@ActiveProfiles("cucumber") // Use the cucumber profile for testing
@CucumberOptions(
    features = "src/test/resources/features/auth.feature", // points to src/test/resources/features/
    glue = "com.inholland.bank.cucumber_testing", // ✅ PACKAGE, not path
    plugin = {"pretty"}
    //, "json:target/cucumber-report.json"
)
public class AuthFeatureTest {
}
