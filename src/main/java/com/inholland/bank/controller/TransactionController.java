package com.inholland.bank.controller;

import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.dto.*;
import com.inholland.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponseDTO> result = transactionService.getAllTransactions(pageable);
            return new ResponseEntity<>(result, HttpStatus.OK);
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
    public ResponseEntity<TransferResponseDTO> transferBetweenAccounts(@RequestBody TransferRequestDTO dto) {
        try {
            transactionService.transferFunds(dto);

            TransferResponseDTO response = new TransferResponseDTO("success", "Transfer completed.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            TransferResponseDTO response = new TransferResponseDTO("error", "Transfer failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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

    @GetMapping("/accounts/{accountId}/filter")
    public ResponseEntity<Page<TransactionDTO>> filterTransactions(
            @PathVariable Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            TransactionFilterDTO filter = new TransactionFilterDTO();
            filter.setStartDate(startDate);
            filter.setEndDate(endDate);
            filter.setMinAmount(minAmount);
            filter.setMaxAmount(maxAmount);
            filter.setIban(iban);

            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionDTO> result = transactionService.filterTransactions(accountId, filter, pageable);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/atm/deposit")
    public ResponseEntity<?> depositWithTransaction(@RequestBody AtmDepositRequestDTO request) {
        try {
            BigDecimal newBalance = transactionService.depositWithTransaction(
                    request.getAccountId(),
                    request.getAmount()
            );
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
