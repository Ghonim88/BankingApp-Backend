package com.inholland.bank.controller;

import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.dto.TransactionDTO;
import com.inholland.bank.model.dto.TransactionFilterDTO;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.model.dto.AtmDepositRequestDTO;
import com.inholland.bank.model.dto.AtmWithdrawRequestDTO;
import com.inholland.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        try {
            List<TransactionDTO> transactions = transactionService.getAllTransactions();
            return new ResponseEntity<>(transactions, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }
    // GET /api/transactions/customer/{id}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Transaction>> getCustomerTransactionHistory(@PathVariable Long customerId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionHistoryByCustomerId(customerId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST /api/transactions/transfer
    @PostMapping("/transfer")
    public ResponseEntity<String> transferBetweenAccounts(@RequestBody TransferRequestDTO dto) {
        try {
            transactionService.transferFunds(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transfer completed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountId(@PathVariable Long accountId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByAccountId(accountId);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/accounts/{accountId}/filter")
    public ResponseEntity<List<TransactionDTO>> filterTransactions(
            @PathVariable Long accountId,
            @RequestBody TransactionFilterDTO filter) {
        try {
            List<TransactionDTO> result = transactionService.filterTransactions(accountId, filter);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/atm/deposit")
    public ResponseEntity<?> depositWithTransaction(@RequestBody AtmDepositRequestDTO request) {
        try {
            BigDecimal newBalance = transactionService.depositWithTransaction(request.getAccountId(), BigDecimal.valueOf(50));
            return ResponseEntity.ok(Map.of("newBalance", newBalance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/atm/withdraw")
    public ResponseEntity<?> withdrawWithTransaction(@RequestBody AtmWithdrawRequestDTO request) {
        try {
            BigDecimal newBalance = transactionService.withdrawWithTransaction(request.getAccountId(), request.getAmount());
            return ResponseEntity.ok(Map.of("newBalance", newBalance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
