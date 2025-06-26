package com.inholland.bank.cucumber_testing;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@ActiveProfiles("cucumber")
@CucumberOptions(
        features = "src/test/resources/features/account-creation.feature",
        glue = "com.inholland.bank.cucumber_testing",
        plugin = {"pretty"}
)
public class AccountCreationFeatureTest {
}
