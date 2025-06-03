package com.inholland.bank.repository;

import com.inholland.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    boolean existsByIban(String iban);

    Optional<Account> findByIban(String iban);

}
