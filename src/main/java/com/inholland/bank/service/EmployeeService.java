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

        List<Account> accounts = accountRepository.findByCustomer(customer);

        return accounts.stream()
                .flatMap(account ->
                        transactionRepository.findByAccount(account).stream()
                )
                .toList();
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

    public void transferFundsBetweenAccounts(String fromIban, String toIban, double amount) {
        validateAmount(amount);

        Account from = getAccountOrThrow(fromIban, "Source account not found");
        Account to = getAccountOrThrow(toIban, "Destination account not found");

        validateSufficientBalance(from, amount);
        validateAbsoluteLimit(from, amount);
        validateDailyLimit(from, amount);

        performTransfer(from, to, amount);
        recordTransaction(from, fromIban, toIban, amount);
    }

    private void validateAmount(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
    }

    private Account getAccountOrThrow(String iban, String errorMsg) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException(errorMsg));
    }

    private void validateSufficientBalance(Account from, double amount) {
        if (from.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient balance in source account");
    }

    private void validateAbsoluteLimit(Account from, double amount) {
        if (amount > from.getAbsoluteTransferLimit())
            throw new IllegalArgumentException("Amount exceeds absolute transfer limit for this account");
    }

    private void validateDailyLimit(Account from, double amount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Transaction> todaysTransactions = transactionRepository.findByAccountAndCreatedAtBetween(from, startOfDay, now);
        double totalTransferredToday = todaysTransactions.stream()
                .mapToDouble(Transaction::getTransactionAmount)
                .sum();

        if (totalTransferredToday + amount > from.getDailyTransferLimit())
            throw new IllegalArgumentException("Amount exceeds daily transfer limit for this account");
    }

    private void performTransfer(Account from, Account to, double amount) {
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        accountRepository.save(from);
        accountRepository.save(to);
    }

    private void recordTransaction(Account from, String fromIban, String toIban, double amount) {
        Transaction transaction = new Transaction();
        transaction.setAccount(from);
        transaction.setSenderIban(fromIban);
        transaction.setReceiverIban(toIban);
        transaction.setTransactionAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

}
