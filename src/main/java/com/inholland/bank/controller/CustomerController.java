package com.inholland.bank.controller;

import com.inholland.bank.model.AccountStatus;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.model.dto.CustomerIbanDTO;
import com.inholland.bank.service.CustomerService;
import com.inholland.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        try {
            List<CustomerDTO> customers = customerService.getAllCustomers();
            return new ResponseEntity<>(customers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CustomerDTO>> getPendingCustomers() {
        try {
            List<CustomerDTO> pendingCustomers = customerService.getCustomersByStatus(AccountStatus.Pending);
            return new ResponseEntity<>(pendingCustomers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verified")
    public ResponseEntity<List<CustomerDTO>> getVerifiedCustomers() {
        try {
            List<CustomerDTO> verifiedCustomers = customerService.getCustomersByStatus(AccountStatus.Verified);
            return new ResponseEntity<>(verifiedCustomers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/closed")
    public ResponseEntity<List<CustomerDTO>> getClosedCustomers() {
        try {
            List<CustomerDTO> closedCustomers = customerService.getCustomersByStatus(AccountStatus.Closed);
            return new ResponseEntity<>(closedCustomers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
        try {
            CustomerDTO customer = customerService.getCustomerById(customerId);
            return new ResponseEntity<>(customer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateAccountStatus(id, customerDTO.getAccountStatus());
            return new ResponseEntity<>(updatedCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerIbanDTO>> searchByName(@RequestParam String name) {
        System.out.println("this is the name from search" +  name);
        List<CustomerIbanDTO> result = customerService.searchCustomersByName(name);

        return ResponseEntity.ok(result);
    }
}
