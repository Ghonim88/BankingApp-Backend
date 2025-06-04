package com.inholland.bank.controller;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.User;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.model.dto.LoginDTO;
import com.inholland.bank.security.CustomUserDetails;
import com.inholland.bank.security.JwtTokenUtil;
import com.inholland.bank.service.CustomerService;
import com.inholland.bank.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @Autowired
  private UserService userService;
  @Autowired
  private CustomerService customerService;
  @Autowired private AuthenticationManager authenticationManager;

  @PostMapping("/login")
  public ResponseEntity<Object> authenticateUser(@RequestBody LoginDTO loginDto) {
    if (loginDto.getEmail() == null
        || loginDto.getEmail().isEmpty()
        || loginDto.getPassword() == null
        || loginDto.getPassword().isEmpty()) {
      return ResponseEntity.badRequest().body("Email and password are required");
    }
    try {
      // 🔐 Authenticate user using Spring Security
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginDto.getEmail(),
                  loginDto.getPassword()) // UserDetailsServiceImpl get in place
              );
      // ✅ Generate token using the authenticated user's details
      String token = jwtTokenUtil.generateToken(authentication);
      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      return ResponseEntity.ok(response); // Only the token is sent in the response

    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }
  }
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
  public ResponseEntity<Object> getLoggedIn(Authentication authentication) {
    try {
      if (authentication == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Authentication is missing" + authentication);
      }

      // Retrieve the User object from CustomUserDetails
      CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
      User user = customUserDetails.getUser();
      // Delegate the logic to the service layer
      Object userData = userService.getLoggedInUser(user);
      return ResponseEntity.ok(userData);

    } catch (Exception e) {
      e.printStackTrace(); // Print the full stack trace to the console
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: " + e.getMessage());
    }
  }
}
