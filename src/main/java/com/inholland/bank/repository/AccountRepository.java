package com.inholland.bank.repository;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIban(String iban);

    List<Account> findByCustomer(Customer customer);

}
