package com.inholland.bank.service;

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
  private  CustomerRepository customerRepository;
  private  PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.customerRepository = customerRepository;
    this.passwordEncoder = passwordEncoder;
  }
  public User registerNewUser(User user) throws Exception {
    if (user.() == null || user.getPassword() == null) {
        throw new Exception("Email and password cannot be null");
    }

    // Check if the email already exists in the database
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new Exception("Email already exists");
    }

    // Encrypt password before saving
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Save the user
    return userRepository.save(user);
}

}
