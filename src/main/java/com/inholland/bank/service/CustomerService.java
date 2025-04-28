package com.inholland.bank.service;

import com.inholland.bank.exceptions.BsnAlreadyExistsException;
import com.inholland.bank.exceptions.EmailAlreadyExistsException;
import com.inholland.bank.exceptions.PhoneAlreadyExistsException;
import org.springframework.stereotype.Service;
import com.inholland.bank.model.Customer;
import com.inholland.bank.repository.UserRepository;
import com.inholland.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
public class CustomerService {
  @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer registerNewCustomer(Customer customer) throws Exception {

        // Check if the email already exists in the database
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(customer.getEmail());
        }
      if (customerRepository.findByPhoneNumber(customer.getPhoneNumber()).isPresent()) {
      throw new PhoneAlreadyExistsException(customer.getPhoneNumber());
      }

      // Check if the BSN already exists in the database
      if (customerRepository.findByBsn(customer.getBsn()).isPresent()) {
      throw new BsnAlreadyExistsException(customer.getBsn());
      }

        // Encrypt password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        // Save the customer
        return customerRepository.save(customer);
    }
}
