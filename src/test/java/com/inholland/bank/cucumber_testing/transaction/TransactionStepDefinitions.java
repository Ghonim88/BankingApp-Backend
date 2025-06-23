package com.inholland.bank.cucumber_testing.transaction;

import com.inholland.bank.config.DatabaseSeeder;
import com.inholland.bank.model.Account;
import com.inholland.bank.model.AccountType;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.cucumber_testing.CommonStepDefinitions;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionStepDefinitions {

    @Autowired
    private DatabaseSeeder seeder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonStepDefinitions commonSteps;

    private Account senderAccount;
    private Account receiverAccount;
    private ResponseEntity<String> response;

    private final String baseUrl = "http://localhost:8080/api/transactions/transfer";

    @Before
    public void setup() {
        seeder.seedAll();
    }

    @Given("customer A and customer B exist with CHECKING accounts")
    public void load_two_customers_with_accounts() {
        List<Customer> verified = customerRepository.findAll().stream()
                .filter(c -> c.getAccountStatus().name().equals("Verified"))
                .toList();

        assertTrue(verified.size() >= 2, "Expected at least 2 verified customers");

        senderAccount = accountRepository.findByCustomer(verified.get(0)).stream()
                .filter(a -> a.getAccountType() == AccountType.CHECKING)
                .findFirst().orElseThrow();

        receiverAccount = accountRepository.findByCustomer(verified.get(1)).stream()
                .filter(a -> a.getAccountType() == AccountType.CHECKING)
                .findFirst().orElseThrow();

        assertNotNull(senderAccount.getIban());
        assertNotNull(receiverAccount.getIban());
    }

    @When("I transfer {double} from customer A to customer B")
    public void i_transfer_funds(Double amount) {
        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setFromIban(senderAccount.getIban());
        dto.setToIban(receiverAccount.getIban());
        dto.setAmount(BigDecimal.valueOf(amount));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferRequestDTO> request = new HttpEntity<>(dto, headers);

        response = restTemplate.postForEntity(baseUrl, request, String.class);
        commonSteps.setResponse(response);
    }

    @Then("the transfer should succeed with status code {int}")
    public void check_status(int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCodeValue(), "Unexpected HTTP status");
    }
}
