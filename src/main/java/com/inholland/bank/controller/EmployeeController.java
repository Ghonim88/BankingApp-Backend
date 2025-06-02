package com.inholland.bank.controller;

import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.service.EmployeeService;
import com.inholland.bank.model.dto.TransferRequestDTO;
import com.inholland.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return employeeService.getAllCustomers();
    }

    @GetMapping("/customers/{customerId}/transactions")
    public List<Transaction> getCustomerTransactionHistory(@PathVariable Long customerId) {
        return transactionService.getTransactionHistoryByCustomerId(customerId);
    }

    @GetMapping("/customers/unapproved")
    public List<Customer> getUnapprovedCustomers() {
        return employeeService.getUnapprovedCustomers();
    }

    @PutMapping("/customers/{customerId}/close")
    public ResponseEntity<String> closeCustomerAccount(@PathVariable Long customerId) {
        employeeService.closeCustomerAccount(customerId);
        return ResponseEntity.ok("Customer account closed.");
    }

    @PostMapping("/transfer")
    public String transferBetweenAccounts(@RequestBody Transaction transaction) {
        transactionService.transferFunds(transaction);
        return "Transfer completed.";
    }


}
