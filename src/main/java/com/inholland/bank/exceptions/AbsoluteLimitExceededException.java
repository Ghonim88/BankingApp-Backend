package com.inholland.bank.exceptions;

public class AbsoluteLimitExceededException extends RuntimeException {
  public AbsoluteLimitExceededException() {
    super("Transaction would reduce account below its absolute transfer limit.");
  }
}

