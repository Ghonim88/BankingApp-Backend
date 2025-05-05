package com.inholland.bank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inholland.bank.model.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByEmail(String email);

  Optional<Customer> findByPhoneNumber(String phoneNumber);
  Optional<Customer> findByBsn(String bsn);
  Optional<Customer> findById(Long id);
}
