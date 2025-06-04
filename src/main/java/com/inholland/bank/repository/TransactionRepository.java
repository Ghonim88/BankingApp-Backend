package com.inholland.bank.repository;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndCreatedAtBetween(Account account, LocalDateTime start, LocalDateTime end);

}
