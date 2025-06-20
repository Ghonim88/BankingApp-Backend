package com.inholland.bank.controller;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.AccountDTO;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inholland.bank.model.dto.AtmDepositRequestDTO;
import com.inholland.bank.model.dto.AtmWithdrawRequestDTO;
import java.math.BigDecimal;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService, AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        try {
            List<AccountDTO> accounts = accountService.getAllAccounts();
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<List<AccountDTO>> createAccountsForCustomer(@RequestBody List<AccountDTO> accountDTOs) {
        try {
            List<AccountDTO> response = accountService.createAccounts(accountDTOs);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/generate-iban")
    public ResponseEntity<String> generatePreviewIban() {
        try {
            String iban = accountService.generateUniqueIban();
            return new ResponseEntity<>(iban, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        try {
            AccountDTO account = accountService.getAccountById(id);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAccount(@PathVariable Long id, @RequestBody AccountDTO dto) {
        try {
            accountService.updateAccount(id, dto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomerId(@PathVariable Long customerId) {
        try {
            List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
            List<AccountDTO> accountDTOs = accounts.stream()
                    .map(accountService::convertToDTO)
                    .toList();
            return new ResponseEntity<>(accountDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/atm/deposit")
    public ResponseEntity<?> depositToAccount(@RequestBody AtmDepositRequestDTO request) {
        try {
            BigDecimal newBalance = accountService.depositFixedAmount(request.getAccountId(), BigDecimal.valueOf(50));
            return ResponseEntity.ok().body(Map.of("newBalance", newBalance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/atm/withdraw")
    public ResponseEntity<?> withdrawFromAccount(@RequestBody AtmWithdrawRequestDTO request) {
        try {
            BigDecimal newBalance = accountService.withdrawAmount(request.getAccountId(), request.getAmount());
            return ResponseEntity.ok().body(Map.of("newBalance", newBalance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

