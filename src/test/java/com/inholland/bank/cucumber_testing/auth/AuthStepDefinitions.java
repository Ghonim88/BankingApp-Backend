package com.inholland.bank.cucumber_testing.auth;

import com.inholland.bank.cucumber_testing.CommonStepDefinitions;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AuthStepDefinitions {

  private final String baseUrl = "http://localhost:8080/";
  private String requestBody;
  private ResponseEntity<String> response;

  @Autowired
  private CommonStepDefinitions commonSteps;
  @Autowired
  private RestTemplate restTemplate;


                                            /* REGISTRATION */
  @Given("I provide registration details with email {string}, password {string}, first name {string}, last name {string}, BSN {string}, and phone number {string}")
  public void i_provide_registration_details(String email, String password, String firstName, String lastName, String bsn, String phoneNumber) {
    requestBody = String.format(
        "{\"email\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", \"bsn\": \"%s\", \"phoneNumber\": \"%s\"}",
        email, password, firstName, lastName, bsn, phoneNumber
    );
  }
  @When("I send a POST request to {string}")
  public void i_send_a_post_request(String endpoint) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON); // Set Content-Type to application/json
    HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      try {
        response = restTemplate.postForEntity(baseUrl + endpoint, entity, String.class);
        commonSteps.setResponse(response);
      } catch (HttpClientErrorException e) {
        // Handle 401 Unauthorized explicitly
        response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        commonSteps.setResponse(response);
      }
  }

  @Then("I should receive a {int} status code")
  public void i_should_receive_status_code(int expectedStatus) {
    assertEquals(expectedStatus, response.getStatusCodeValue());
  }
  @Then("the response should confirm the customer is registered")
  public void response_should_confirm_registration() {
    // Log the response body for debugging
    System.out.println("Response Body: " + response.getBody());
    assertTrue(response.getBody().contains("\"firstName\":\"John\""),
        "Expected response to contain the customer's first name, but it did not.");
  }

                           /*LOGIN*/
@Given("I provide login details with email {string} and password {string}")
public void i_provide_login_details(String email, String password) {
 requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
}


  @Then("the response should contain a valid JWT token")
  public void response_should_contain_jwt() {
    assertTrue(response.getBody().contains("token"),
        "Expected response to contain a valid JWT token, but it did not.");
  }

  @Then("the response should indicate invalid credentials")
  public void response_should_indicate_invalid_credentials() {
    // Log the response body and status code for debugging
    System.out.println("Response Status Code: " + response.getStatusCodeValue());
    System.out.println("Response Body: " + response.getBody());

    // Check if the response body contains the expected error message
    assertTrue(
        response.getStatusCodeValue() == 401 && response.getBody().isEmpty() ,
        "Expected response to indicate invalid credentials, but it did not. Response body was null, empty, or did not contain the expected message."
    );
  }
}