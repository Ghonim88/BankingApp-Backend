package com.inholland.bank.cucumber_testing.atm;

import com.inholland.bank.cucumber_testing.CommonStepDefinitions;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AtmStepDefinitions {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonStepDefinitions commonSteps;

    private final String baseUrl = "http://localhost:8080/";
    private String jwtToken;
    private Long selectedAccountId;
    private ResponseEntity<String> response;

    @Given("I am logged in as an employee for ATM")
    public void i_am_logged_in_as_employee() {
        String loginBody = "{\"email\": \"john@gmail.com\", \"password\": \"Password11!\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(loginBody, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl + "auth/login", entity, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        jwtToken = JsonPath.read(loginResponse.getBody(), "$.token");
    }

    @Given("I select an existing CHECKING account for customer {int}")
    public void select_existing_checking_account(int customerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "accounts/customer/" + customerId,
                HttpMethod.GET,
                entity,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Integer> accountIds = JsonPath.read(response.getBody(), "$[?(@.accountType == 'CHECKING')].accountId");
        assertFalse(accountIds.isEmpty(), "No CHECKING account found for customer " + customerId);
        selectedAccountId = accountIds.get(0).longValue();
    }

    @When("I deposit {double} into the ATM")
    public void depositIntoAtm(Double amount) {
        Map<String, Object> body = new HashMap<>();
        body.put("accountId", selectedAccountId);
        body.put("amount", amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            response = restTemplate.postForEntity(baseUrl + "api/transactions/atm/deposit", entity, String.class);
            commonSteps.setResponse(response);
        } catch (HttpClientErrorException e) {
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            commonSteps.setResponse(response);
        }
    }

    @When("I withdraw {double} from the ATM")
    public void withdrawFromAtm(Double amount) {
        Map<String, Object> body = new HashMap<>();
        body.put("accountId", selectedAccountId);
        body.put("amount", amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            response = restTemplate.postForEntity(baseUrl + "api/transactions/atm/withdraw", entity, String.class);
            commonSteps.setResponse(response);
        } catch (HttpClientErrorException e) {
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            commonSteps.setResponse(response);
        }
    }

    @Then("the ATM response should show new balance {double}")
    public void checkNewBalance(Double expectedBalance) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BigDecimal balance = new BigDecimal(JsonPath.read(response.getBody(), "$.newBalance").toString());
        assertEquals(0, balance.compareTo(BigDecimal.valueOf(expectedBalance)));
    }

    @Then("the ATM response should contain error message {string}")
    public void checkErrorMessage(String expectedMessage) {
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(expectedMessage), "Expected error message not found in response");
    }
}
