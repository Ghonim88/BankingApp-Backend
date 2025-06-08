package com.inholland.bank.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String iban) {
        super("Account " + iban + " has insufficient funds for this transaction.");
    }
}