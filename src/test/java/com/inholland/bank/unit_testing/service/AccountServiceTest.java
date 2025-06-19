package com.inholland.bank.unit_testing.service;

import com.inholland.bank.model.Account;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.service.AccountService;
import com.inholland.bank.service.IbanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private IbanService ibanService;
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        accountRepository = mock(AccountRepository.class);
        customerRepository = mock(CustomerRepository.class);
        ibanService = mock(IbanService.class);
        accountService = new AccountService();

        // Use reflection to inject mocks into private fields
        injectPrivateField(accountService, "accountRepository", accountRepository);
        injectPrivateField(accountService, "customerRepository", customerRepository);
        injectPrivateField(accountService, "ibanService", ibanService);
    }

    private void injectPrivateField(Object target, String fieldName, Object toInject) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, toInject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDepositFixedAmount() {
        // Arrange
        Long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal depositAmount = new BigDecimal("50.00");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        BigDecimal newBalance = accountService.depositFixedAmount(accountId, depositAmount);

        // Assert
        assertEquals(new BigDecimal("150.00"), newBalance);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testWithdrawAmount_Success() {
        // Arrange
        Long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        BigDecimal newBalance = accountService.withdrawAmount(accountId, withdrawAmount);

        // Assert
        assertEquals(new BigDecimal("150.00"), newBalance);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testWithdrawAmount_InsufficientFunds() {
        // Arrange
        Long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal("30.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.withdrawAmount(accountId, withdrawAmount);
        });

        assertEquals("Insufficient funds", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
