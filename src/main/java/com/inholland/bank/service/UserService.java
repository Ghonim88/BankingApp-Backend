package com.inholland.bank.service;

import com.inholland.bank.model.Customer;
import org.springframework.stereotype.Service;
import com.inholland.bank.model.User;
import com.inholland.bank.repository.UserRepository;
import com.inholland.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }
  public Optional<User> login(String email, String password) {
    // Fetch customer by email
    Optional<User> userOptional = userRepository.findByEmail(email);

    if (userOptional.isPresent()) {
      User user = userOptional.get();

      // Check if the password matches
      if (passwordEncoder.matches(password, user.getPassword())) {
        // Password matches, return the customer
        return Optional.of(user);
      } else {
        // Password doesn't match
        return Optional.empty();
      }
    }

    // Customer not found
    return Optional.empty();
  }
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }


}
