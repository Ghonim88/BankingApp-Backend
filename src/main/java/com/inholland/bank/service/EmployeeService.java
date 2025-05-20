package com.inholland.bank.service;

import com.inholland.bank.model.*;
import com.inholland.bank.model.dto.EmployeeDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.EmployeeRepository;
import com.inholland.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private  EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, CustomerRepository customerRepository,
                           TransactionRepository transactionRepository,
                           AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }
    public Employee registerNewEmployee(EmployeeDTO employeeDto) {
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(employeeDto.getPassword());
        employeeDto.setPassword(hashedPassword);
        // Map DTO to Entity
        Employee employee = new Employee();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPassword(employeeDto.getPassword());

      return employeeRepository.save(employee);
    }
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Transaction> getTransactionHistoryByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return transactionRepository.findByCustomer(customer);
    }

    public List<Customer> getUnapprovedCustomers() {
        return customerRepository.findByAccountStatus(AccountStatus.Pending);
    }

    public void closeCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setAccountStatus(AccountStatus.Closed);
        customerRepository.save(customer);
    }

    public void transferFundsBetweenCustomers(String fromIban, String toIban, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");

        Account from = accountRepository.findByIban(fromIban)
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account to = accountRepository.findByIban(toIban)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (from.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient balance in source account");

        Customer sender = from.getCustomer();

        // 1. Absolute Limit Check
        if (amount > sender.getTransactionLimit())
            throw new IllegalArgumentException("Amount exceeds absolute transfer limit");

        // 2. Daily Limit Check
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Transaction> todaysTransactions = transactionRepository.findByCustomerAndCreatedAtBetween(sender, startOfDay, now);

        double totalTransferredToday = todaysTransactions.stream()
                .mapToDouble(Transaction::getTransactionAmount)
                .sum();

        if (totalTransferredToday + amount > sender.getDailyTransferLimit())
            throw new IllegalArgumentException("Amount exceeds daily transfer limit");

        // Perform Transfer
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction transaction = new Transaction();
        transaction.setCustomer(sender);
        transaction.setSenderIban(fromIban);
        transaction.setReceiverIban(toIban);
        transaction.setTransactionAmount(amount);
        transaction.setCreatedAt(now);
        transactionRepository.save(transaction);
    }


}
