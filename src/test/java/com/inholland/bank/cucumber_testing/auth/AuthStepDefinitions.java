package com.inholland.bank.cucumber_testing.auth;

import com.inholland.bank.cucumber_testing.CommonStepDefinitions;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthStepDefinitions {
  @Autowired
  private CommonStepDefinitions commonSteps;

  private String requestBody;
  private ResponseEntity<String> response;

  @Given("I provide registration details with email {string} and password {string}")
  @Given("I provide login details with email {string} and password {string}")
  public void i_provide_user_details(String email, String password) {
    requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
  }

  @When("I send a POST request to {string}")
  public void i_send_a_post_request(String endpoint) {
    commonSteps.setBaseUrl();  // Ensure baseUrl is set
    response = commonSteps.getResponseEntity(endpoint, requestBody);
  }

  @Then("I should receive a {int} status code")
  public void i_should_receive_status_code(int expectedStatus) {
    assertEquals(expectedStatus, response.getStatusCodeValue());
  }

  @Then("the response should confirm the customer is registered")
  public void response_should_confirm_registration() {
    assertTrue(response.getBody().contains("registered")); // Adjust this based on actual response
  }

  @Then("the response should contain a valid JWT token")
  public void response_should_contain_jwt() {
    assertTrue(response.getBody().contains("token")); // or a better check
  }

  @Then("the response should indicate the customer already exists")
  public void response_should_indicate_duplicate() {
    assertTrue(response.getBody().toLowerCase().contains("already exists"));
  }

  @Then("the response should indicate invalid credentials")
  public void response_should_indicate_invalid_credentials() {
    assertTrue(response.getBody().toLowerCase().contains("invalid credentials"));
  }
}
