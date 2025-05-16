package com.inholland.bank.controller;
import com.inholland.bank.model.dto.LoginDTO;
import com.inholland.bank.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private AuthenticationManager authenticationManager;

  @PostMapping("/login")
  public ResponseEntity<Object> authenticateUser(@RequestBody LoginDTO loginDto) {
    if (loginDto.getEmail() == null || loginDto.getEmail().isEmpty() ||
        loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
      return ResponseEntity.badRequest().body("Email and password are required");
    }

    try {
      // 🔐 Authenticate user using Spring Security
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()) //UserDetailsServiceImpl get in place
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
}

