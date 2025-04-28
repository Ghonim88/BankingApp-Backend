package com.inholland.bank.controller;

import com.inholland.bank.model.Customer;
import com.inholland.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

  @Autowired
  private CustomerService customerService;
  @PostMapping("/register")
  public ResponseEntity<Object> registerCustomer(@RequestBody Customer customer) {
    try {
      Customer newCustomer = customerService.registerNewCustomer(customer);
      return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
