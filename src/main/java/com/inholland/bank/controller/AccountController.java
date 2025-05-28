package com.inholland.bank.controller;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.dto.AccountDTO;
import com.inholland.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

@PostMapping
public ResponseEntity<List<AccountDTO>> createAccountsForCustomer(@RequestBody List<AccountDTO> accountDTOs) {
    try {
        List<Account> savedAccounts = accountService.createAccounts(accountDTOs);
        List<AccountDTO> response = savedAccounts.stream()
                .map(accountService::convertToDTO)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
}
