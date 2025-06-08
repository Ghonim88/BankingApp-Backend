package com.inholland.bank.service;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bank.exceptions.AbsoluteLimitExceededException;
import com.inholland.bank.exceptions.DailyLimitExceededException;
import com.inholland.bank.exceptions.InsufficientFundsException;

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

    public void transferFunds(TransferRequestDTO dto) {
        Account from = getAccountOrThrow(dto.getFromIban(), "Sender not found");
        Account to = getAccountOrThrow(dto.getToIban(), "Receiver not found");
        BigDecimal amount = dto.getAmount();

        validateAmount(amount);
        validateSufficientBalance(from, amount);
        validateAbsoluteLimit(from, amount);
        validateDailyLimit(from, amount);

        performTransfer(from, to, amount);

        // Create and record transactions
        Transaction senderTransaction = buildTransaction(from, dto.getFromIban(), dto.getToIban(), amount);
        Transaction receiverTransaction = buildTransaction(to, dto.getFromIban(), dto.getToIban(), amount);

        recordTransaction(senderTransaction);
        recordTransaction(receiverTransaction);
    }

    public List<Transaction> getTransactionsForToday(Account account) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return transactionRepository.findByAccountAndCreatedAtBetween(account, startOfDay, now);
    }

    public List<Transaction> getTransactionHistoryByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Account> accounts = accountRepository.findByCustomer(customer);
        return accounts.stream()
                .flatMap(account -> transactionRepository.findByAccount(account).stream())
                .toList();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
    }

    private Account getAccountOrThrow(String iban, String errorMsg) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException(errorMsg));
    }

    private void validateSufficientBalance(Account from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(from.getIban());
        }
    }

    private void validateAbsoluteLimit(Account from, BigDecimal amount) {
        BigDecimal resultingBalance = from.getBalance().subtract(amount);
        if (resultingBalance.compareTo(from.getAbsoluteTransferLimit()) < 0) {
            throw new AbsoluteLimitExceededException(from.getIban());
        }
    }

    private void validateDailyLimit(Account from, BigDecimal amount) {
        BigDecimal totalTransferredToday = getTransactionsForToday(from).stream()
                .map(Transaction::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalTransferredToday.add(amount).compareTo(from.getDailyTransferLimit()) > 0) {
            throw new DailyLimitExceededException(from.getIban());
        }
    }

    private void performTransfer(Account from, Account to, BigDecimal amount) {
        BigDecimal newFromBalance = from.getBalance().subtract(amount);
        BigDecimal newToBalance = to.getBalance().add(amount);

        from.setBalance(newFromBalance);
        to.setBalance(newToBalance);

        accountRepository.save(from);
        accountRepository.save(to);
    }

    private Transaction buildTransaction(Account account, String senderIban, String receiverIban, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setSenderIban(senderIban);
        transaction.setReceiverIban(receiverIban);
        transaction.setTransactionAmount(amount);
        transaction.setAccount(account);
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }

    private void recordTransaction(Transaction transaction) {
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }

        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Transaction must have an associated account before saving.");
        }

        if (transaction.getTransactionAmount() == null || transaction.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be a positive value.");
        }

        if (transaction.getSenderIban() == null || transaction.getReceiverIban() == null) {
            throw new IllegalArgumentException("Transaction must include both sender and receiver IBANs.");
        }

        transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}
