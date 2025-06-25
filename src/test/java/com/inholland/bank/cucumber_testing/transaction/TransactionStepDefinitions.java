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

    @Given("I am logged in as an employee for transfer")
    public void i_am_logged_in_as_an_employee_for_transfer() {
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

    @Given("I get the IBAN of customer 2's CHECKING account")
    public void get_customer1_checking_iban() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "accounts/customer/2",
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

    @Given("I get the IBAN of customer 3's CHECKING account")
    public void get_customer2_savings_iban() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "accounts/customer/3",
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> ibans = JsonPath.read(response.getBody(), "$.[?(@.accountType == 'CHECKING')].iban");
        assertFalse(ibans.isEmpty(), "No CHECKING account found for customer 2");
        receiverIban = ibans.get(0);
        System.out.println("Receiver IBAN: " + receiverIban);
    }

    @When("I transfer {double} from the CHECKING to the CHECKING account")
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

    @Given("a customer with low balance exists")
    public void customer_with_low_balance_exists() {
        // Already assumed seeded; ensure balance is low or mock it
        // You can use a known customer (e.g., ID 4) with €0 balance
    }

    @Given("I get the IBAN of that customer's CHECKING account")
    public void get_low_balance_customer_iban() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "accounts/customer/4",  // use a known ID
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> ibans = JsonPath.read(response.getBody(), "$.[?(@.accountType == 'CHECKING')].iban");
        assertFalse(ibans.isEmpty(), "No CHECKING account found");
        senderIban = ibans.get(0);
    }

    @When("I attempt to transfer {double} from the CHECKING to the CHECKING account")
    public void i_attempt_transfer_invalid(double amount) {
        i_transfer_funds(amount); // reusing logic
    }

    @Then("the response should contain {string}")
    public void response_should_contain(String expectedContent) {
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(expectedContent), "Response does not contain: " + expectedContent);
    }

    @Given("I get the transaction history for customer {int}")
    public void get_transaction_history(int customerId) {
        ResponseEntity<String> historyResponse = restTemplate.exchange(
                baseUrl + "api/transactions/customer/" + customerId,
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                String.class
        );
        response = historyResponse;
    }

    @Then("the transaction history response should contain at least {int} transaction")
    public void transaction_history_should_have_at_least(int count) {
        List<?> transactions = JsonPath.read(response.getBody(), "$");
        assertTrue(transactions.size() >= count, "Expected at least " + count + " transactions, got " + transactions.size());
    }

}
