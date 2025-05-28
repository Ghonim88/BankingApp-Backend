package com.inholland.bank.service;


import com.inholland.bank.exceptions.CustomerNotFoundException;
import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.AccountDTO;
import com.inholland.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bank.repository.CustomerRepository;


import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private IbanService ibanService;
    @Autowired
    private CustomerRepository customerRepository;

    public List<Account> createAccounts(List<AccountDTO> dtos) {
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
        //dto.setCurrency(account.getCurrency());
        dto.setDailyTransferLimit(account.getDailyTransferLimit());
        dto.setAbsoluteTransferLimit(account.getAbsoluteTransferLimit());
        dto.setAccountType(account.getAccountType());
        dto.setCustomerId(account.getCustomer().getUserId());
        return dto;
    }

    private Account convertToEntity(AccountDTO dto) {
        Account account = new Account();
        account.setAccountType(dto.getAccountType());
        account.setDailyTransferLimit(dto.getDailyTransferLimit());
        account.setAbsoluteTransferLimit(dto.getAbsoluteTransferLimit());
        account.setBalance(dto.getBalance());
        //account.setCurrency("EUR");
        account.setIban(generateUniqueIban());

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.getCustomerId()));
        account.setCustomer(customer);

        return account;
    }
}
