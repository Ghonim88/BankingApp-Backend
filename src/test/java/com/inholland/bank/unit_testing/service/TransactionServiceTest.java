package com.inholland.bank.unit_testing.service;

import com.inholland.bank.exceptions.DailyLimitExceededException;
import com.inholland.bank.exceptions.InvalidAmountException;
import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.User;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import com.inholland.bank.repository.UserRepository;
import com.inholland.bank.service.TransactionExecutor;
import com.inholland.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private TransactionExecutor transactionExecutor;
    private TransactionService transactionService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        customerRepository = mock(CustomerRepository.class);
        transactionExecutor = mock(TransactionExecutor.class);
        userRepository = mock(UserRepository.class);
        transactionService = new TransactionService(transactionRepository, accountRepository, customerRepository, transactionExecutor, userRepository);
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

    @Test
    public void testTransferFunds_Successful() {
        String fromIban = "NL01INHO0000000001";
        String toIban = "NL01INHO0000000002";
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAccount = new Account();
        fromAccount.setIban(fromIban);
        fromAccount.setBalance(new BigDecimal("500.00"));
        fromAccount.setAbsoluteTransferLimit(new BigDecimal("-500.00"));
        fromAccount.setDailyTransferLimit(new BigDecimal("1000.00"));

        Account toAccount = new Account();
        toAccount.setIban(toIban);
        toAccount.setBalance(new BigDecimal("300.00"));

        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setFromIban(fromIban);
        dto.setToIban(toIban);
        dto.setAmount(amount);

        when(accountRepository.findByIban(fromIban)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(toIban)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.findByFromAccountAndCreatedAtBetween(eq(fromAccount), any(), any())).thenReturn(List.of());

        transactionService.transferFunds(dto);

        verify(transactionExecutor, times(1)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testTransferFunds_InvalidAmount_ThrowsException() {
        Account fromAccount = new Account();
        fromAccount.setIban("FROM");
        fromAccount.setBalance(new BigDecimal("100.00"));
        fromAccount.setAbsoluteTransferLimit(new BigDecimal("-100.00"));
        fromAccount.setDailyTransferLimit(new BigDecimal("500.00"));

        Account toAccount = new Account();
        toAccount.setIban("TO");
        toAccount.setBalance(new BigDecimal("100.00"));

        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setFromIban("FROM");
        dto.setToIban("TO");
        dto.setAmount(BigDecimal.ZERO); // Invalid

        when(accountRepository.findByIban("FROM")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban("TO")).thenReturn(Optional.of(toAccount));

        assertThrows(InvalidAmountException.class, () -> {
            transactionService.transferFunds(dto);
        });

        verify(transactionExecutor, never()).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testTransferFunds_ExceedsDailyLimit_ThrowsException() {
        String fromIban = "FROM";
        String toIban = "TO";
        BigDecimal transferAmount = new BigDecimal("600.00");

        Account from = new Account();
        from.setIban(fromIban);
        from.setBalance(new BigDecimal("1000.00"));
        from.setDailyTransferLimit(new BigDecimal("1000.00"));
        from.setAbsoluteTransferLimit(new BigDecimal("-100.00"));

        Account to = new Account();
        to.setIban(toIban);
        to.setBalance(new BigDecimal("500.00"));

        Transaction existing = new Transaction();
        existing.setTransactionAmount(new BigDecimal("500.00"));

        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setFromIban(fromIban);
        dto.setToIban(toIban);
        dto.setAmount(transferAmount);

        when(accountRepository.findByIban(fromIban)).thenReturn(Optional.of(from));
        when(accountRepository.findByIban(toIban)).thenReturn(Optional.of(to));
        when(transactionRepository.findByFromAccountAndCreatedAtBetween(eq(from), any(), any()))
                .thenReturn(List.of(existing));

        assertThrows(DailyLimitExceededException.class, () -> {
            transactionService.transferFunds(dto);
        });

        verify(transactionExecutor, never()).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testGetTransactionHistoryByCustomerId() {
        Long customerId = 1L;

        Customer customer = new Customer();
        Account account1 = new Account();
        Account account2 = new Account();
        account1.setIban("IBAN1");
        account2.setIban("IBAN2");

        customer.setAccounts(List.of(account1, account2));

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        Transaction t3 = new Transaction();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountRepository.findByCustomer(customer)).thenReturn(List.of(account1, account2));
        when(transactionRepository.findByFromAccountOrToAccount(account1, account1)).thenReturn(List.of(t1));
        when(transactionRepository.findByFromAccountOrToAccount(account2, account2)).thenReturn(List.of(t2, t3));

        var result = transactionService.getTransactionHistoryByCustomerId(customerId);

        assertEquals(3, result.size());
        verify(customerRepository).findById(customerId);
        verify(accountRepository).findByCustomer(customer);
    }
}
