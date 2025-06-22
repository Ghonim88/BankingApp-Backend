package com.inholland.bank.service;

import com.inholland.bank.exceptions.*;
import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.dto.TransactionDTO;
import com.inholland.bank.model.dto.TransactionFilterDTO;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.model.TransactionType;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final TransactionExecutor transactionExecutor;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CustomerRepository customerRepository,
                              TransactionExecutor transactionExecutor) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionExecutor = transactionExecutor;
    }

    public Page<TransactionDTO> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public void transferFunds(TransferRequestDTO dto) {

        Account from = getAccountOrThrow(dto.getFromIban());
        Account to = getAccountOrThrow(dto.getToIban());

        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setTransactionAmount(dto.getAmount());
        transaction.setTransactionType(TransactionType.TRANSFER);  // <-- NEW

        this.transferFunds(transaction);
    }

    public void transferFunds(Transaction transaction) {
        validateAmount(transaction.getTransactionAmount());

        Account from = transaction.getFromAccount();
        Account to = transaction.getToAccount();
        BigDecimal amount = transaction.getTransactionAmount();

        validateSufficientBalance(from, amount);
        validateAbsoluteLimit(from, amount);
        validateDailyLimit(from, amount);

        transactionExecutor.executeTransaction(transaction);
    }

    public List<Transaction> getTransactionsForToday(Account account) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return transactionRepository.findByFromAccountAndCreatedAtBetween(account, startOfDay, now);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

    private Account getAccountOrThrow(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account with IBAN " + iban + " was not found."));
    }


    public List<Transaction> getTransactionHistoryByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        List<Account> accounts = accountRepository.findByCustomer(customer);

        return accounts.stream()
                .flatMap(account ->
                        transactionRepository.findByFromAccountOrToAccount(account, account).stream()
                ).toList();
    }

    private void validateSufficientBalance(Account from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException();
    }

    private void validateAbsoluteLimit(Account from, BigDecimal amount) {
        BigDecimal remainingBalance = from.getBalance().subtract(amount);
        if (remainingBalance.compareTo(from.getAbsoluteTransferLimit()) < 0)
            throw new AbsoluteLimitExceededException();
    }

    private void validateDailyLimit(Account from, BigDecimal amount) {
        BigDecimal totalTransferredToday = getTransactionsForToday(from).stream()
                .map(Transaction::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalTransferredToday.add(amount).compareTo(from.getDailyTransferLimit()) > 0)
            throw new DailyLimitExceededException();
    }

    public TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setSenderIban(transaction.getFromAccount().getIban());
        dto.setReceiverIban(transaction.getToAccount().getIban());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setTransactionType(transaction.getTransactionType());

        return dto;
    }

    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(account, account);
        return transactions.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<TransactionDTO> filterTransactions(Long accountId, TransactionFilterDTO filter) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(account, account);

        return transactions.stream()
                .filter(t -> filter.getStartDate() == null || !t.getCreatedAt().toLocalDate().isBefore(filter.getStartDate()))
                .filter(t -> filter.getEndDate() == null || !t.getCreatedAt().toLocalDate().isAfter(filter.getEndDate()))
                .filter(t -> filter.getMinAmount() == null || t.getTransactionAmount().compareTo(filter.getMinAmount()) >= 0)
                .filter(t -> filter.getMaxAmount() == null || t.getTransactionAmount().compareTo(filter.getMaxAmount()) <= 0)
                .filter(t -> filter.getIban() == null || t.getFromAccount().getIban().equals(filter.getIban()) || t.getToAccount().getIban().equals(filter.getIban()))
                .map(this::convertToDTO)
                .toList();
    }

    // ----------------- ATM Specific Code -----------------

    public BigDecimal depositWithTransaction(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(account); // ATM deposit: from = to = account itself
        transaction.setToAccount(account);
        transaction.setTransactionAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.ATM_DEPOSIT);

        transactionExecutor.executeTransaction(transaction);

        return account.getBalance();
    }

    public BigDecimal withdrawWithTransaction(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setTransactionAmount(amount.negate()); // withdrawals show as negative
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.ATM_WITHDRAWAL);

        transactionExecutor.executeTransaction(transaction);

        return account.getBalance();
    }
}