package com.inholland.bank.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long userId) {
        super(String.format("The customer with the id: '%s' was not found. Please try a different search.", userId));
    }
}
