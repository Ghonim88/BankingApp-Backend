// TransactionServiceTest.java (UNIT TEST)
package com.inholland.bank.unit_testing.service;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import com.inholland.bank.service.TransactionExecutor;
import com.inholland.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private TransactionExecutor transactionExecutor;
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        customerRepository = mock(CustomerRepository.class);
        transactionExecutor = mock(TransactionExecutor.class);
        transactionService = new TransactionService(transactionRepository, accountRepository, customerRepository, transactionExecutor);
    }

    @Test
    public void testDepositWithTransaction() {
        Long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal depositAmount = new BigDecimal("50.00");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        BigDecimal newBalance = transactionService.depositWithTransaction(accountId, depositAmount);

        assertEquals(new BigDecimal("150.00"), newBalance);
        verify(accountRepository, times(1)).save(account);
        verify(transactionExecutor, times(1)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testWithdrawWithTransaction_Success() {
        Long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        BigDecimal newBalance = transactionService.withdrawWithTransaction(accountId, withdrawAmount);

        assertEquals(new BigDecimal("150.00"), newBalance);
        verify(accountRepository, times(1)).save(account);
        verify(transactionExecutor, times(1)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testWithdrawWithTransaction_InsufficientFunds() {
        Long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal("30.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.withdrawWithTransaction(accountId, withdrawAmount);
        });

        assertEquals("Insufficient funds", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionExecutor, never()).executeTransaction(any(Transaction.class));
    }
}
