package com.inholland.bank.controller;

import com.inholland.bank.model.AccountStatus;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.model.dto.CustomerIbanDTO;
import com.inholland.bank.service.CustomerService;
import com.inholland.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CustomerDTO> result = customerService.getAllCustomers(pageable);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<CustomerDTO>> getCustomersByStatus(
            @PathVariable String status, // will auto-convert if enum name matches
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            AccountStatus accountStatus = AccountStatus.valueOf(
                    status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase()
            );
            Pageable pageable = PageRequest.of(page, size);
            Page<CustomerDTO> result = customerService.getCustomersByStatus(accountStatus, pageable);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
        try {
            CustomerDTO customer = customerService.getCustomerById(customerId);
            return new ResponseEntity<>(customer, HttpStatus.OK);
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
