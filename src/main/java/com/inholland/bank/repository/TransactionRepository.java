package com.inholland.bank.repository;

import com.inholland.bank.model.Transaction;
import com.inholland.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomer(Customer customer);

    List<Transaction> findByCustomerAndCreatedAtBetween(Customer customer, LocalDateTime start, LocalDateTime end);

}
