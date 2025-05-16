package com.inholland.bank.controller;

import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

  @Autowired
  private CustomerService customerService;
  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @PostMapping("/register")
  public ResponseEntity<Object> registerCustomer(@RequestBody CustomerDTO customerDto) {
    try {
      Customer newCustomer = customerService.registerNewCustomer(customerDto);
      return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/me")
  public ResponseEntity<Object> getLoggedInCustomer(Authentication authentication) {
    try {
      logger.info("Authentication: {}", authentication);

      if (authentication == null) {
        logger.error("Authentication is null");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is missing" + authentication);
      }
      logger.info("Authentication principal: {}", authentication.getPrincipal());
      logger.info("Authentication name: {}", authentication.getName());
      logger.info("Authentication authorities: {}", authentication.getAuthorities());

      String email = authentication.getName();
      logger.info("Logged in email: {}", email);


      Customer customer = customerService.findByEmail(email);
      return ResponseEntity.ok(customer);
    } catch (Exception e) {
      e.printStackTrace(); // Print the full stack trace to the console
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: " + e.getMessage());
    }
  }
}
