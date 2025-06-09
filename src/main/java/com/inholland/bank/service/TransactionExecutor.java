package com.inholland.bank.service;

import com.inholland.bank.model.Transaction;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionExecutor {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionExecutor(AccountRepository accountRepository,
                               TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void executeTransaction(Transaction transaction) {
        transaction.getFromAccount().setBalance(
                transaction.getFromAccount().getBalance().subtract(transaction.getTransactionAmount())
        );
        transaction.getToAccount().setBalance(
                transaction.getToAccount().getBalance().add(transaction.getTransactionAmount())
        );

        accountRepository.save(transaction.getFromAccount());
        accountRepository.save(transaction.getToAccount());

        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }

        transactionRepository.save(transaction);
    }
}
