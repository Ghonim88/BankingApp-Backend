package com.inholland.bank.exceptions;

public class InvalidAmountException extends RuntimeException {
  public InvalidAmountException() {
    super("Amount must be provided and greater than zero.");
  }
}

