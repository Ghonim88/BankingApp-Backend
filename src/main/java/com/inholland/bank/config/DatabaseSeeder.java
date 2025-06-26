package com.inholland.bank.config;

import com.inholland.bank.model.*;
import com.inholland.bank.model.dto.AccountDTO;
import com.inholland.bank.model.dto.EmployeeDTO;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.repository.UserRepository;
import com.inholland.bank.service.AccountService;
import com.inholland.bank.service.EmployeeService;
import com.inholland.bank.service.CustomerService;
import com.inholland.bank.service.TransactionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder {

    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public DatabaseSeeder(EmployeeService employeeService,
                          CustomerService customerService,
                          CustomerRepository customerRepository,
                          AccountRepository accountRepository,
                          AccountService accountService,
                          TransactionService transactionService, UserRepository userRepository) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
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
        customer1.setEmail("sophie.martens@email.com");
        customer1.setPassword("Password1!");
        customer1.setFirstName("Sophie");
        customer1.setLastName("Martens");
        customer1.setPhoneNumber("+31610123456");
        customer1.setBsn("123456782");
        customerService.registerNewCustomer(customer1);

        CustomerDTO customer2 = new CustomerDTO();
        customer2.setEmail("liam.dejong@email.com");
        customer2.setPassword("Password2!");
        customer2.setFirstName("Liam");
        customer2.setLastName("De Jong");
        customer2.setPhoneNumber("+31610234567");
        customer2.setBsn("234567891");
        customerService.registerNewCustomer(customer2);

        CustomerDTO customer3 = new CustomerDTO();
        customer3.setEmail("emma.vanrijn@email.com");
        customer3.setPassword("Password3!");
        customer3.setFirstName("Emma");
        customer3.setLastName("Van Rijn");
        customer3.setPhoneNumber("+31610345678");
        customer3.setBsn("345678901");
        customerService.registerNewCustomer(customer3);

        CustomerDTO customer4 = new CustomerDTO();
        customer4.setEmail("noah.visser@email.com");
        customer4.setPassword("Password4!");
        customer4.setFirstName("Noah");
        customer4.setLastName("Visser");
        customer4.setPhoneNumber("+31610456789");
        customer4.setBsn("456789012");
        customerService.registerNewCustomer(customer4);

        CustomerDTO customer5 = new CustomerDTO();
        customer5.setEmail("julia.koster@email.com");
        customer5.setPassword("Password5!");
        customer5.setFirstName("Julia");
        customer5.setLastName("Koster");
        customer5.setPhoneNumber("+31610567890");
        customer5.setBsn("567890123");
        customerService.registerNewCustomer(customer5);

        CustomerDTO customer6 = new CustomerDTO();
        customer6.setEmail("david.bos@email.com");
        customer6.setPassword("Password6!");
        customer6.setFirstName("David");
        customer6.setLastName("Bos");
        customer6.setPhoneNumber("+31610678901");
        customer6.setBsn("678901234");
        customerService.registerNewCustomer(customer6);

        CustomerDTO customer7 = new CustomerDTO();
        customer7.setEmail("zoe.vandermeer@email.com");
        customer7.setPassword("Password7!");
        customer7.setFirstName("Zoe");
        customer7.setLastName("Van der Meer");
        customer7.setPhoneNumber("+31610789012");
        customer7.setBsn("789012345");
        customerService.registerNewCustomer(customer7);

        CustomerDTO customer8 = new CustomerDTO();
        customer8.setEmail("milan.steen@email.com");
        customer8.setPassword("Password8!");
        customer8.setFirstName("Milan");
        customer8.setLastName("Steen");
        customer8.setPhoneNumber("+31610890123");
        customer8.setBsn("890123456");
        customerService.registerNewCustomer(customer8);

        CustomerDTO customer9 = new CustomerDTO();
        customer9.setEmail("nina.scholten@email.com");
        customer9.setPassword("Password9!");
        customer9.setFirstName("Nina");
        customer9.setLastName("Scholten");
        customer9.setPhoneNumber("+31610901234");
        customer9.setBsn("901234567");
        customerService.registerNewCustomer(customer9);

        CustomerDTO customer10 = new CustomerDTO();
        customer10.setEmail("sebastian.bakker@email.com");
        customer10.setPassword("Password10!");
        customer10.setFirstName("Sebastian");
        customer10.setLastName("Bakker");
        customer10.setPhoneNumber("+31611012345");
        customer10.setBsn("012345678");
        customerService.registerNewCustomer(customer10);

        List<Customer> allCustomers = customerRepository.findAll();

        for (int i = 0; i < 7 && i < allCustomers.size(); i++) {
            customerService.updateAccountStatus(allCustomers.get(i).getUserId(), AccountStatus.Verified);
        }
    }

    public void seedAccounts() {
        List<Customer> verifiedCustomers = customerRepository.findAll()
                .stream()
                .filter(c -> c.getAccountStatus() == AccountStatus.Verified)
                .toList();

        for (Customer customer : verifiedCustomers) {
            List<AccountDTO> accounts = new ArrayList<>();

            AccountDTO savings = new AccountDTO();
            savings.setAccountType(AccountType.SAVINGS);
            savings.setBalance(BigDecimal.valueOf(5000));
            savings.setDailyTransferLimit(BigDecimal.valueOf(5000));
            savings.setAbsoluteTransferLimit(BigDecimal.valueOf(100));
            savings.setCustomerId(customer.getUserId());
            accounts.add(savings);

            AccountDTO checking = new AccountDTO();
            checking.setAccountType(AccountType.CHECKING);
            checking.setBalance(BigDecimal.valueOf(500));
            checking.setDailyTransferLimit(BigDecimal.valueOf(500));
            checking.setAbsoluteTransferLimit(BigDecimal.valueOf(10));
            checking.setCustomerId(customer.getUserId());
            accounts.add(checking);

            accountService.createAccounts(accounts);
        }
    }

    public void seedTransactions() {
        List<Account> allAccounts = accountRepository.findAll();

        if (allAccounts.size() < 2) {
            System.out.println("Not enough accounts to create transactions.");
            return;
        }

        for (int i = 0; i < 10; i++) {
            Account sender = allAccounts.get(i % allAccounts.size());
            Account receiver = allAccounts.get((i + 1) % allAccounts.size());

            // Avoid sending to self
            if (sender.getIban().equals(receiver.getIban())) continue;

            Transaction transaction = new Transaction();
            transaction.setFromAccount(sender);
            transaction.setToAccount(receiver);
            transaction.setTransactionAmount(BigDecimal.valueOf(5)); // Variable amount
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setTransactionType(TransactionType.TRANSFER);
            User initiator = userRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("User not found for seeding"));
            transaction.setInitiator(initiator);

            try {
                transactionService.transferFunds(transaction);
                System.out.println("Transaction " + (i + 1) + " created.");
            } catch (Exception e) {
                System.out.println("Failed to create transaction " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    public void seedAll() {
        seedEmployees();
        seedCustomers();
        seedAccounts();
        seedTransactions();
    }


}
