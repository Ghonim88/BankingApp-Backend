package com.inholland.bank.repository;

import com.inholland.bank.model.Account;
import com.inholland.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find all transactions where the given account is either sender or receiver
    List<Transaction> findByFromAccountOrToAccount(Account from, Account to);

    // Find today's transactions where the account is the sender
    List<Transaction> findByFromAccountAndCreatedAtBetween(Account account, LocalDateTime start, LocalDateTime end);
}
