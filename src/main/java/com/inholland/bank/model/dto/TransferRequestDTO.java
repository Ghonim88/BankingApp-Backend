package com.inholland.bank.model.dto;

public class TransferRequestDTO {
    private String fromIban;
    private String toIban;
    private double amount;

    public TransferRequestDTO() {
        // Default constructor
    }

    public String getFromIban() {
        return fromIban;
    }

    public void setFromIban(String fromIban) {
        this.fromIban = fromIban;
    }

    public String getToIban() {
        return toIban;
    }

    public void setToIban(String toIban) {
        this.toIban = toIban;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
