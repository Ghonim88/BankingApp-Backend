package com.inholland.bank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inholland.bank.model.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByEmail(String email);

  Optional<Object> findByPhoneNumber(String phoneNumber);
  Optional<Object> findByBsn(String bsn);
}
