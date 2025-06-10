package com.inholland.bank.exceptions;

public class DailyLimitExceededException extends RuntimeException {
  public DailyLimitExceededException() {
    super("Daily transfer limit exceeded for this account.");
  }
}


