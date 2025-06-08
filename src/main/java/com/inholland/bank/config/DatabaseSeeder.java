package com.inholland.bank.config;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.AccountType;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.EmployeeDTO;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.service.EmployeeService;
import com.inholland.bank.service.CustomerService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DatabaseSeeder {

    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public DatabaseSeeder(EmployeeService employeeService,
                          CustomerService customerService,
                          CustomerRepository customerRepository,
                          AccountRepository accountRepository) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }


    public void seedEmployees() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("john@gmail.com");
        employeeDTO.setPassword("Password11!");
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeService.registerNewEmployee(employeeDTO);
    }

    public void seedCustomers() {
        CustomerDTO customer1 = new CustomerDTO();
        customer1.setEmail("customer1@gmail.com");
        customer1.setPassword("Password1!");
        customer1.setFirstName("Alice");
        customer1.setLastName("Johnson");
        customer1.setPhoneNumber("+31611111111");
        customer1.setBsn("111111111");
        customerService.registerNewCustomer(customer1);

        CustomerDTO customer2 = new CustomerDTO();
        customer2.setEmail("customer2@gmail.com");
        customer2.setPassword("Password2!");
        customer2.setFirstName("Bob");
        customer2.setLastName("Smith");
        customer2.setPhoneNumber("+31622222222");
        customer2.setBsn("222222222");
        customerService.registerNewCustomer(customer2);

        CustomerDTO customer3 = new CustomerDTO();
        customer3.setEmail("customer3@gmail.com");
        customer3.setPassword("Password3!");
        customer3.setFirstName("Charlie");
        customer3.setLastName("Brown");
        customer3.setPhoneNumber("+31633333333");
        customer3.setBsn("333333333");
        customerService.registerNewCustomer(customer3);

        CustomerDTO customer4 = new CustomerDTO();
        customer4.setEmail("customer4@gmail.com");
        customer4.setPassword("Password4!");
        customer4.setFirstName("Diana");
        customer4.setLastName("Miller");
        customer4.setPhoneNumber("+31644444444");
        customer4.setBsn("444444444");
        customerService.registerNewCustomer(customer4);

        CustomerDTO customer5 = new CustomerDTO();
        customer5.setEmail("customer5@gmail.com");
        customer5.setPassword("Password5!");
        customer5.setFirstName("Edward");
        customer5.setLastName("Wilson");
        customer5.setPhoneNumber("+31655555555");
        customer5.setBsn("555555555");
        customerService.registerNewCustomer(customer5);
    }

    public void seedTestAccount() {
        Customer customer = customerRepository.findByEmail("customer1@gmail.com");

        if (customer != null) {
            // Check if the customer already has accounts
            List<Account> existingAccounts = accountRepository.findAll().stream()
                    .filter(acc -> acc.getCustomer().getUserId().equals(customer.getUserId()))
                    .toList();

            boolean hasChecking = existingAccounts.stream().anyMatch(a -> a.getAccountType() == AccountType.CHECKING);
            boolean hasSavings = existingAccounts.stream().anyMatch(a -> a.getAccountType() == AccountType.SAVINGS);

            if (!hasChecking) {
                Account checking = new Account(AccountType.CHECKING, customer);
                checking.setBalance(BigDecimal.valueOf(500));
                checking.setDailyTransferLimit(BigDecimal.valueOf(300));
                checking.setAbsoluteTransferLimit(BigDecimal.valueOf(0));
                accountRepository.save(checking);
            }

            if (!hasSavings) {
                Account savings = new Account(AccountType.SAVINGS, customer);
                savings.setBalance(BigDecimal.valueOf(1000));
                savings.setDailyTransferLimit(BigDecimal.valueOf(500));
                savings.setAbsoluteTransferLimit(BigDecimal.valueOf(0));
                accountRepository.save(savings);
            }
        }
    }


}
