package com.inholland.bank.service;

import com.inholland.bank.exceptions.UserNotFoundException;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Employee;
import com.inholland.bank.model.UserRole;
import com.inholland.bank.repository.EmployeeRepository;
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
  private CustomerRepository customerRepository;
  @Autowired
  private EmployeeRepository employeeRepository;
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
  public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  public Object getLoggedInUser(User user) {
    Long userId = user.getUserId();
    String role = user.getUserRole().name();

    if (UserRole.CUSTOMER.name().equals(role)) {
      Optional<Customer> customer = customerRepository.findById(userId);
      if (customer.isEmpty()) {
        throw new UserNotFoundException("Customer with ID " + userId + " not found");
      }
      return customer;
    } else if (UserRole.EMPLOYEE.name().equals(role)) {
      Optional<Employee> employee = employeeRepository.findById(userId);
      if (employee.isEmpty()) {
        throw new UserNotFoundException("Employee with ID " + userId + " not found");
      }
      return employee;
    }
    throw new RuntimeException("Invalid role");
  }
}
