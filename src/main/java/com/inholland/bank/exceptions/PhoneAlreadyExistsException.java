package com.inholland.bank.exceptions;

public class PhoneAlreadyExistsException extends RuntimeException {

  public PhoneAlreadyExistsException(String phoneNumber) {
    super(String.format("The phone number '%s' is already registered. Please use a different phone number.", phoneNumber));
  }
  }