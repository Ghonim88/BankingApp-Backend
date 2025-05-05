package com.inholland.bank.controller;

import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @PostMapping("/register") //TODO: DOES THE EMPLOYEE NEEDS TO BE REGISTERED OR JUST THE CUSTOMER? ASK DANIEL!!
  public ResponseEntity<Object> registerCustomer(@RequestBody CustomerDTO customerDto) {
    try {
      Customer newCustomer = customerService.registerNewCustomer(customerDto);
      return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
//  @GetMapping()
//  public ResponseEntity<?> getLoggedInCustomer(Authentication authentication) {//TODO: work on the login part
//    String email = authentication.getName();  // Comes from JWT "sub"
//    Customer customer = customerService.findByEmail(email)
//       .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
//    return ResponseEntity.ok(customer);
//  }

}
