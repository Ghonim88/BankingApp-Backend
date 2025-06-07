package com.inholland.bank.service;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.dto.TransactionDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;

    }

    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public void transferFunds(Transaction transaction) {
        validateAmount(transaction.getTransactionAmount());

        Account from = getAccountOrThrow(transaction.getSenderIban(), "Source account not found");
        Account to = getAccountOrThrow(transaction.getReceiverIban(), "Destination account not found");

        BigDecimal amount = transaction.getTransactionAmount();

        validateSufficientBalance(from, amount);
        validateAbsoluteLimit(from, amount);
        validateDailyLimit(from, amount);

        performTransfer(from, to, amount);

        transaction.setAccount(from);
        transaction.setCreatedAt(LocalDateTime.now());

        recordTransaction(transaction);
    }

    public List<Transaction> getTransactionsForToday(Account account) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return transactionRepository.findByAccountAndCreatedAtBetween(account, startOfDay, now);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
    }

    private Account getAccountOrThrow(String iban, String errorMsg) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException(errorMsg));
    }

    public List<Transaction> getTransactionHistoryByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Account> accounts = accountRepository.findByCustomer(customer);
        return accounts.stream()
                .flatMap(account -> transactionRepository.findByAccount(account).stream())
                .toList();
    }

    private void validateSufficientBalance(Account from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient balance in source account");
    }

    private void validateAbsoluteLimit(Account from, BigDecimal amount) {
        if (amount.compareTo(from.getAbsoluteTransferLimit()) > 0)
            throw new IllegalArgumentException("Amount exceeds absolute transfer limit for this account");
    }

    private void validateDailyLimit(Account from, BigDecimal amount) {
        BigDecimal totalTransferredToday = getTransactionsForToday(from).stream()
                .map(Transaction::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalTransferredToday.add(amount).compareTo(from.getDailyTransferLimit()) > 0)
            throw new IllegalArgumentException("Amount exceeds daily transfer limit for this account");
    }

    private void performTransfer(Account from, Account to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);
    }

    private void recordTransaction(Transaction transaction) {
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }

        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Transaction must have an associated account before saving.");
        }

        transactionRepository.save(transaction);
    }

    public TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setSenderIban(transaction.getSenderIban());
        dto.setReceiverIban(transaction.getReceiverIban());
        dto.setCreatedAt(transaction.getCreatedAt());

        return dto;
    }
}
