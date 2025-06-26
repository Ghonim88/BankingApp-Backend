package com.inholland.bank.service;

import com.inholland.bank.exceptions.*;
import com.inholland.bank.model.*;
import com.inholland.bank.model.dto.TransactionDTO;
import com.inholland.bank.model.dto.TransactionFilterDTO;
import com.inholland.bank.model.dto.TransactionResponseDTO;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.TransactionRepository;
import com.inholland.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionExecutor transactionExecutor;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CustomerRepository customerRepository,
                              TransactionExecutor transactionExecutor, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionExecutor = transactionExecutor;
        this.userRepository = userRepository;
    }

    public Page<TransactionResponseDTO> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public void transferFunds(TransferRequestDTO dto) {

        Account from = getAccountOrThrow(dto.getFromIban());
        Account to = getAccountOrThrow(dto.getToIban());

        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setTransactionAmount(dto.getAmount());
        transaction.setTransactionType(TransactionType.TRANSFER);  // <-- NEW
        transaction.setInitiator(userRepository.findById(dto.getInitiatorId()).orElse(null));
        this.transferFunds(transaction);
    }

    public void transferFunds(Transaction transaction) {
        validateAmount(transaction.getTransactionAmount());

        Account from = transaction.getFromAccount();
        Account to = transaction.getToAccount();
        BigDecimal amount = transaction.getTransactionAmount();
        User initiator = transaction.getInitiator();

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
        dto.setInitiatorId(transaction.getInitiator().getUserId());
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

    public Page<TransactionDTO> filterTransactions(Long accountId, TransactionFilterDTO filter, Pageable pageable) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));


        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(account, account);
        Stream<Transaction> filtered = transactions.stream();

        if (filter.getStartDate() != null)
            filtered = filtered.filter(t -> !t.getCreatedAt().toLocalDate().isBefore(filter.getStartDate()));
        if (filter.getEndDate() != null)
            filtered = filtered.filter(t -> !t.getCreatedAt().toLocalDate().isAfter(filter.getEndDate()));
        if (filter.getMinAmount() != null)
            filtered = filtered.filter(t -> t.getTransactionAmount().compareTo(filter.getMinAmount()) >= 0);
        if (filter.getMaxAmount() != null)
            filtered = filtered.filter(t -> t.getTransactionAmount().compareTo(filter.getMaxAmount()) <= 0);
        if (filter.getIban() != null)
            filtered = filtered.filter(t ->
                    t.getFromAccount().getIban().equals(filter.getIban()) ||
                            t.getToAccount().getIban().equals(filter.getIban())
            );

        List<TransactionDTO> dtos = filtered.map(this::convertToDTO).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        List<TransactionDTO> pagedList = dtos.subList(start, end);

        return new PageImpl<>(pagedList, pageable, dtos.size());
    }

    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getTransactionId());
        dto.setFromIban(transaction.getFromAccount().getIban());
        dto.setToIban(transaction.getToAccount().getIban());
        dto.setAmount(transaction.getTransactionAmount());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setTransactionType(transaction.getTransactionType().toString());

        // Set full name of sender
        if (transaction.getFromAccount().getCustomer() != null) {
            User sender = transaction.getFromAccount().getCustomer();
            dto.setFromAccountHolderName(sender.getFirstName() + " " + sender.getLastName());
        }

        // Set full name of receiver
        if (transaction.getToAccount().getCustomer() != null) {
            User receiver = transaction.getToAccount().getCustomer();
            dto.setToAccountHolderName(receiver.getFirstName() + " " + receiver.getLastName());
        }

        // Initiator (could be employee or customer)

        if (transaction.getInitiator() != null) {
            dto.setInitiatorName(
                    transaction.getInitiator().getFirstName() + " " + transaction.getInitiator().getLastName()
            );
            dto.setInitiatorRole(transaction.getInitiator().getUserRole().toString());
        }

        return dto;
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

        // Set initiator as account owner
        if (account.getCustomer() != null) {
            transaction.setInitiator(account.getCustomer());
        }

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

        // Set initiator as account owner
        if (account.getCustomer() != null) {
            transaction.setInitiator(account.getCustomer());
        }

        transactionExecutor.executeTransaction(transaction);

        return account.getBalance();
    }
}