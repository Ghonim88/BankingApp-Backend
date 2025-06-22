package com.inholland.bank.cucumber_testing.atm;

import com.inholland.bank.model.*;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import com.inholland.bank.service.TransactionService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AtmStepDefinitions {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    private Account account;
    private Exception lastException;
    private BigDecimal depositAmount;

    @Given("an account exists with balance {double}")
    public void an_account_exists_with_balance(Double balance) {
        // Create customer
        Customer customer = new Customer();
        customer.setEmail("test" + UUID.randomUUID() + "@example.com");
        customer.setPassword("password");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPhoneNumber(UUID.randomUUID().toString().substring(0,10));
        customer.setBsn(UUID.randomUUID().toString().substring(0,9));
        customer.setAccountStatus(AccountStatus.Verified);
        customer.setUserRole(UserRole.CUSTOMER);
        customerRepository.save(customer);

        // Create account
        account = new Account();
        account.setBalance(BigDecimal.valueOf(balance));
        account.setIban("NL01INHO" + UUID.randomUUID().toString().substring(0,10));
        account.setDailyTransferLimit(BigDecimal.valueOf(1000));
        account.setAbsoluteTransferLimit(BigDecimal.valueOf(-500));
        account.setAccountType(AccountType.CHECKING);
        account.setCustomer(customer);
        accountRepository.save(account);
    }

    @When("I deposit {double} into the account")
    public void i_deposit_into_the_account(Double amount) {
        lastException = null;
        depositAmount = BigDecimal.valueOf(amount);
        try {
            account.setBalance(account.getBalance().add(depositAmount));
            accountRepository.save(account);

            // Refresh after update
            account = accountRepository.findById(account.getAccountId()).orElseThrow();
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("I withdraw {double} from the account")
    public void i_withdraw_from_the_account(Double amount) {
        lastException = null;
        try {
            // Create dummy destination account to simulate withdrawal
            Account dummyDestination = new Account();
            dummyDestination.setBalance(BigDecimal.ZERO);
            dummyDestination.setIban("NL01INHO" + UUID.randomUUID().toString().substring(0,10));
            dummyDestination.setDailyTransferLimit(BigDecimal.valueOf(1000));
            dummyDestination.setAbsoluteTransferLimit(BigDecimal.valueOf(-500));
            dummyDestination.setAccountType(AccountType.CHECKING);
            dummyDestination.setCustomer(account.getCustomer());
            accountRepository.save(dummyDestination);

            Transaction transaction = new Transaction();
            transaction.setFromAccount(account);
            transaction.setToAccount(dummyDestination);
            transaction.setTransactionAmount(BigDecimal.valueOf(amount));
            transaction.setCreatedAt(LocalDateTime.now());
            transactionService.transferFunds(transaction);

            // Refresh account after transaction
            account = accountRepository.findById(account.getAccountId()).orElseThrow();
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the new balance should be {double}")
    public void the_new_balance_should_be(Double expectedBalance) {
        assertEquals(BigDecimal.valueOf(expectedBalance), account.getBalance().setScale(1, BigDecimal.ROUND_HALF_UP));
    }

    @Then("I should receive an insufficient funds error")
    public void i_should_receive_insufficient_funds_error() {
        assertNotNull(lastException);
        assertTrue(lastException.getClass().getSimpleName().contains("InsufficientFundsException"));
    }
}