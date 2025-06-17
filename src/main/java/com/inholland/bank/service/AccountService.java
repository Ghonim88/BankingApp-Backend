package com.inholland.bank.service;


import com.inholland.bank.exceptions.CustomerNotFoundException;
import com.inholland.bank.exceptions.InvalidAccountCreationRequestException;
import com.inholland.bank.model.Account;
import com.inholland.bank.model.AccountType;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.AccountDTO;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bank.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private IbanService ibanService;
    @Autowired
    private CustomerRepository customerRepository;

    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public List<Account> createAccounts(List<AccountDTO> dtos) {
        if (dtos.size() != 2) {
            throw new InvalidAccountCreationRequestException("You cannot create more or less than 2 accounts for a single customer. (one checking, one savings)");
        }

        Set<AccountType> requestedTypes = dtos.stream()
                .map(AccountDTO::getAccountType)
                .collect(Collectors.toSet());

        if (requestedTypes.size() != 2 ||
                !requestedTypes.contains(AccountType.CHECKING) ||
                !requestedTypes.contains(AccountType.SAVINGS)) {
            throw new InvalidAccountCreationRequestException("Both CHECKING and SAVINGS accounts are required.");
        }

        Set<Long> customerIds = dtos.stream()
                .map(AccountDTO::getCustomerId)
                .collect(Collectors.toSet());
        if (customerIds.size() != 1) {
            throw new InvalidAccountCreationRequestException("All accounts must belong to the same customer.");
        }

        Long customerId = customerIds.iterator().next();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));


        List<Account> existingAccounts = customer.getAccounts();
        if (existingAccounts != null && !existingAccounts.isEmpty()) {
            throw new InvalidAccountCreationRequestException("Customer already has accounts. Cannot create more than one CHECKING and one SAVINGS account.");
        }

        List<Account> accounts = dtos.stream()
                .map(this::convertToEntity)
                .toList();

        return accountRepository.saveAll(accounts);
    }

    public String generateUniqueIban() {
        String iban;
        do {
            iban = ibanService.generateIban();
        } while (accountRepository.findByIban(iban).isPresent());
        return iban;
    }

    public AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setAccountId(account.getAccountId());
        dto.setIban(account.getIban());
        dto.setBalance(account.getBalance());
        dto.setDailyTransferLimit(account.getDailyTransferLimit());
        dto.setAbsoluteTransferLimit(account.getAbsoluteTransferLimit());
        dto.setAccountType(account.getAccountType());
        dto.setCustomerId(account.getCustomer().getUserId());
        dto.setOwnerEmail(account.getCustomer().getEmail());

        return dto;
    }

    private Account convertToEntity(AccountDTO dto) {
        Account account = new Account();
        account.setAccountType(dto.getAccountType());
        account.setDailyTransferLimit(dto.getDailyTransferLimit());
        account.setAbsoluteTransferLimit(dto.getAbsoluteTransferLimit());

        //if given an iban, use it, otherwise generate new
        if (dto.getIban() != null && !dto.getIban().isEmpty()) {
            account.setIban(dto.getIban());
        } else {
            account.setIban(generateUniqueIban());
        }

        if(dto.getBalance() != null) {
            account.setBalance(dto.getBalance());
        } else {
            account.setBalance(BigDecimal.ZERO);
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.getCustomerId()));
        account.setCustomer(customer);

        return account;
    }

    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
        return convertToDTO(account);
    }

    public void updateAccount(Long id, AccountDTO dto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));

        if (dto.getDailyTransferLimit() != null) {
            existingAccount.setDailyTransferLimit(dto.getDailyTransferLimit());
        }

        if (dto.getAbsoluteTransferLimit() != null) {
            existingAccount.setAbsoluteTransferLimit(dto.getAbsoluteTransferLimit());
        }

        accountRepository.save(existingAccount);
    }

    public List<Account> getAccountsByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return accountRepository.findByCustomer(customer);
    }

    public BigDecimal depositFixedAmount(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        return account.getBalance();
    }

    public BigDecimal withdrawAmount(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return account.getBalance();
    }

}
