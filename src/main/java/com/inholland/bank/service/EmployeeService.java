package com.inholland.bank.service;

import com.inholland.bank.model.AccountStatus;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.Employee;
import com.inholland.bank.model.UserRole;
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

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
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
        employee.setUserRole(UserRole.EMPLOYEE);

      return employeeRepository.save(employee);
    }

}
