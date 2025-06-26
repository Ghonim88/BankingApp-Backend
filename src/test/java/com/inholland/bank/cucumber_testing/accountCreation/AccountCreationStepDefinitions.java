package com.inholland.bank.cucumber_testing.accountCreation;


import com.inholland.bank.cucumber_testing.CommonStepDefinitions;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountCreationStepDefinitions {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonStepDefinitions commonSteps;

    private final String baseUrl = "http://localhost:8080/";
    private String requestBody;
    private ResponseEntity<String> response;
    private String jwtToken;


    @Given("I am logged in as an employee")
    public void i_am_logged_in_as_an_employee() {
        String loginBody = "{\"email\": \"john@gmail.com\", \"password\": \"Password11!\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(loginBody, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl + "auth/login", entity, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        jwtToken = JsonPath.read(loginResponse.getBody(), "$.token");
    }
    @Given("I prepare two account DTOs for customer with ID {long}: one CHECKING and one SAVINGS")
    public void prepareTwoValidAccounts(Long customerId) {
        requestBody = String.format("""
            [
              {
                "accountType": "CHECKING",
                "customerId": %d,
                "dailyTransferLimit": 1000,
                "absoluteTransferLimit": -100
              },
              {
                "accountType": "SAVINGS",
                "customerId": %d,
                "dailyTransferLimit": 500,
                "absoluteTransferLimit": -50
              }
            ]
            """, customerId, customerId);
    }

    @Given("I prepare one CHECKING account DTO for customer with ID {long}")
    public void prepareOneAccount(Long customerId) {
        requestBody = String.format("""
            [
              {
                "accountType": "CHECKING",
                "customerId": %d,
                "dailyTransferLimit": 1000,
                "absoluteTransferLimit": -100
              }
            ]
            """, customerId);
    }

    @Given("I prepare two account DTOs for different customer IDs")
    public void prepareAccountsWithDifferentCustomers() {
        requestBody = """
            [
              {
                "accountType": "CHECKING",
                "customerId": 10,
                "dailyTransferLimit": 1000,
                "absoluteTransferLimit": -100
              },
              {
                "accountType": "SAVINGS",
                "customerId": 11,
                "dailyTransferLimit": 500,
                "absoluteTransferLimit": -50
              }
            ]
            """;
    }

    @When("I send a POST request to create accounts at {string}")
    public void sendPostRequest(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken); // Use token
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            response = restTemplate.postForEntity(baseUrl + endpoint, entity, String.class);
            commonSteps.setResponse(response);
        } catch (HttpClientErrorException e) {
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            commonSteps.setResponse(response);
        }
    }

    @Then("the account creation response should have status code {int}")
    public void shouldReceiveStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCodeValue());
    }

    @Then("the response should include both account types")
    public void responseShouldIncludeAccountTypes() {
        String body = response.getBody();
        assertTrue(body.contains("CHECKING") && body.contains("SAVINGS"));
    }

    @Then("the response should indicate invalid account creation")
    public void responseShouldIndicateInvalidAccountCreation() {
        System.out.println("Response Status Code: " + response.getStatusCodeValue());
        System.out.println("Response Body: " + response.getBody());

        // Accept even empty responses for 400
        assertTrue(
                response.getStatusCodeValue() == 400 && response.getBody().isEmpty() ,
                "Expected response to indicate invalid account creation, but it did not. Response body was null, empty, or did not contain the expected message."
        );
    }
}
