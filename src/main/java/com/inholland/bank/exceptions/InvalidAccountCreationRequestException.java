package com.inholland.bank.exceptions;

public class InvalidAccountCreationRequestException extends RuntimeException {

    public InvalidAccountCreationRequestException (String message) {
        super(message);
    }
}
