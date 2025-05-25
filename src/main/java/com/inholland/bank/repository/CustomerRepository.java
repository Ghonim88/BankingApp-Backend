package com.inholland.bank.repository;
import com.inholland.bank.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.inholland.bank.model.Customer;
import java.util.List;


import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


  Customer findByEmail(String email); //TODO: double check if you want todo an optional or as it is

  Optional<Customer> findByPhoneNumber(String phoneNumber);
  Optional<Customer> findByBsn(String bsn);
  Optional<Customer> findById(Long id);
  List<Customer> findAll();
  List<Customer> findByAccountStatus(AccountStatus status);

}
