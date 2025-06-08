package com.inholland.bank.exceptions;

public class DailyLimitExceededException extends RuntimeException {
  public DailyLimitExceededException(String iban) {
    super("Daily transfer limit exceeded for account " + iban + ".");
  }
}

