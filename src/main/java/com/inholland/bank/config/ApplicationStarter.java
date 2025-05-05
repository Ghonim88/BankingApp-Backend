package com.inholland.bank.config;

import com.inholland.bank.model.dto.EmployeeDTO;
import com.inholland.bank.service.EmployeeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStarter implements ApplicationRunner {

  private final EmployeeService employeeService;

  public ApplicationStarter(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setEmail("john@gmail.com");
    employeeDTO.setPassword("password");
    employeeDTO.setFirstName("John");
    employeeDTO.setLastName("Doe");
    employeeService.registerNewEmployee(employeeDTO);


  }
}
