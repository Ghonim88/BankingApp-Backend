package com.inholland.bank.controller;

import com.inholland.bank.model.Transaction;
import com.inholland.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // GET /api/transactions/customer/5
    @GetMapping("/customer/{customerId}")
    public List<Transaction> getCustomerTransactionHistory(@PathVariable Long customerId) {
        return transactionService.getTransactionHistoryByCustomerId(customerId);
    }

    // POST /api/transactions/transfer
    @PostMapping("/transfer")
    public String transferBetweenAccounts(@RequestBody Transaction transaction) {
        transactionService.transferFunds(transaction);
        return "Transfer completed.";
    }
}
