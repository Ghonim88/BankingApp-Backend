package com.inholland.bank.service;

import com.inholland.bank.exceptions.BsnAlreadyExistsException;
import com.inholland.bank.exceptions.CustomerNotFoundException;
import com.inholland.bank.exceptions.EmailAlreadyExistsException;
import com.inholland.bank.exceptions.PhoneAlreadyExistsException;
import com.inholland.bank.model.AccountStatus;
import com.inholland.bank.model.dto.CustomerDTO;
import org.springframework.stereotype.Service;
import com.inholland.bank.model.Customer;
import com.inholland.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;


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
    customer.setUserRole(customerDto.getUserRole());

    return customerRepository.save(customer);
  }

  public CustomerDTO updateAccountStatus(Long customerId, AccountStatus newStatus) {
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

    customer.setAccountStatus(newStatus);
    Customer updated = customerRepository.save(customer);

    return convertToDTO(updated);
  }

  public List<CustomerDTO> getAllCustomers() {
    List<Customer> customers = customerRepository.findAll();
    return customers.stream()
            .map(this::convertToDTO)
            .toList();
  }

  public List<CustomerDTO> getCustomersByStatus(AccountStatus status) {
    List<Customer> customers = customerRepository.findByAccountStatus(status);
    return customers.stream()
            .map(this::convertToDTO)
            .toList();
  }

  private CustomerDTO convertToDTO(Customer customer) {
    CustomerDTO dto = new CustomerDTO();
    dto.setUserId(customer.getUserId());
    dto.setFirstName(customer.getFirstName());
    dto.setLastName(customer.getLastName());
    dto.setEmail(customer.getEmail());
    dto.setPhoneNumber(customer.getPhoneNumber());
    dto.setBsn(customer.getBsn());
    dto.setAccountStatus(customer.getAccountStatus());
    dto.setUserRole(customer.getUserRole());
    return dto;
  }

  public CustomerDTO getCustomerById(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
    return convertToDTO(customer);
  }
}

