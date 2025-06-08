package com.inholland.bank.exceptions;

public class AbsoluteLimitExceededException extends RuntimeException {
  public AbsoluteLimitExceededException(String iban) {
    super("Transaction would reduce account " + iban + " below its absolute transfer limit.");
  }
}
