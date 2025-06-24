package com.inholland.bank.cucumber_testing.transaction;

import com.inholland.bank.cucumber_testing.CommonStepDefinitions;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStepDefinitions {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonStepDefinitions commonSteps;

    private final String baseUrl = "http://localhost:8080/";
    private String jwtToken;
    private String senderIban;
    private String receiverIban;
    private ResponseEntity<String> response;

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

    @Given("two customers with verified accounts exist")
    public void customers_with_verified_accounts_exist() {
        // Assumes seeder already created at least 2 verified customers with accounts
    }

    @Given("I get the IBAN of customer 1's CHECKING account")
    public void get_customer1_checking_iban() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "api/accounts/customer/1",
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> ibans = JsonPath.read(response.getBody(), "$.[?(@.accountType == 'CHECKING')].iban");
        assertFalse(ibans.isEmpty(), "No CHECKING account found for customer 1");
        senderIban = ibans.get(0);
        System.out.println("Sender IBAN: " + senderIban);
    }

    @Given("I get the IBAN of customer 2's SAVINGS account")
    public void get_customer2_savings_iban() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "api/accounts/customer/2",
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> ibans = JsonPath.read(response.getBody(), "$.[?(@.accountType == 'SAVINGS')].iban");
        assertFalse(ibans.isEmpty(), "No SAVINGS account found for customer 2");
        receiverIban = ibans.get(0);
        System.out.println("Receiver IBAN: " + receiverIban);
    }

    @When("I transfer {double} from the CHECKING to the SAVINGS account")
    public void i_transfer_funds(double amount) {
        String transferJson = String.format("""
            {
              "fromIban": "%s",
              "toIban": "%s",
              "amount": %.2f
            }
            """, senderIban, receiverIban, amount);

        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(transferJson, headers);

        try {
            response = restTemplate.postForEntity(baseUrl + "api/transactions/transfer", entity, String.class);
        } catch (HttpClientErrorException e) {
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }

        commonSteps.setResponse(response);
    }

    @Then("the transfer response should have status code {int}")
    public void transfer_response_should_have_status(int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCodeValue());
    }

    @Then("the response should confirm the transfer was completed")
    public void response_should_confirm_transfer() {
        assertTrue(response.getBody().contains("Transfer completed"), "Expected transfer confirmation in response");
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        return headers;
    }
}
