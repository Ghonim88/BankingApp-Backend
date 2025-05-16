package com.inholland.bank.service;

import com.inholland.bank.exceptions.BsnAlreadyExistsException;
import com.inholland.bank.exceptions.EmailAlreadyExistsException;
import com.inholland.bank.exceptions.PhoneAlreadyExistsException;
import com.inholland.bank.model.dto.CustomerDTO;
import org.springframework.stereotype.Service;
import com.inholland.bank.model.Customer;
import com.inholland.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class CustomerService {
  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private  PasswordEncoder passwordEncoder;


    public Customer registerNewCustomer(CustomerDTO customerDto) {

        // Check if the email already exists in the database
        if (customerRepository.findByEmail(customerDto.getEmail()) != null) {
      throw new EmailAlreadyExistsException(customerDto.getEmail());
        }
      if (customerRepository.findByPhoneNumber(customerDto.getPhoneNumber()).isPresent()) {
      throw new PhoneAlreadyExistsException(customerDto.getPhoneNumber());
      }

      // Check if the BSN already exists in the database
      if (customerRepository.findByBsn(customerDto.getBsn()).isPresent()) {
      throw new BsnAlreadyExistsException(customerDto.getBsn());
      }

        // Encrypt password before saving
      customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
      // Map DTO to entity
      Customer customer = new Customer();
      customer.setFirstName(customerDto.getFirstName());
      customer.setLastName(customerDto.getLastName());
      customer.setEmail(customerDto.getEmail());
      customer.setPhoneNumber(customerDto.getPhoneNumber());
      customer.setBsn(customerDto.getBsn());
      customer.setPassword(customerDto.getPassword());

      return customerRepository.save(customer);
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }


  public Customer findByEmail(String email) {
    return customerRepository.findByEmail(email); // assuming repository returns Optional
  }
}
