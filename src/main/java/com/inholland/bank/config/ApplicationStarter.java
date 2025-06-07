package com.inholland.bank.config;

import com.inholland.bank.model.dto.EmployeeDTO;
import com.inholland.bank.service.EmployeeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStarter implements ApplicationRunner {
private final DatabaseSeeder seeder;

  public ApplicationStarter(DatabaseSeeder seeder) {
    this.seeder = seeder;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    seeder.seedEmployees();
    seeder.seedCustomers();

  }
}
