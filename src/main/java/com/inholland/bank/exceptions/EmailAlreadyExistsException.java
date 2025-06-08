package com.inholland.bank.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {

  public EmailAlreadyExistsException(String email) {
    super(
        String.format(
            "The email address '%s' is already registered in the system. Please use a different email address.",
            email));
  }
  }
