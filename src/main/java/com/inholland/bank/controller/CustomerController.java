package com.inholland.bank.controller;

import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.service.CustomerService;
import com.inholland.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
        CustomerDTO customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateUser(@PathVariable Long id, @RequestBody CustomerDTO userDTO) {
        CustomerDTO user = customerService.updateAccountStatus(id, userDTO.getAccountStatus());

        return ResponseEntity.ok(user);
    }
}
