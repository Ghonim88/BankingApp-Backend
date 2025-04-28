package com.inholland.bank.exceptions;

public class BsnAlreadyExistsException extends RuntimeException {

  // Constructor that accepts a message
  public BsnAlreadyExistsException(String bsn) {
    super(String.format("The BSN '%s' is already registered. Please use a different BSN.", bsn));
  }
}
