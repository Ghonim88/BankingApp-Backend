package com.inholland.bank.model.dto;

import java.math.BigDecimal;

public class TransferRequestDTO {
    private String fromIban;
    private String toIban;
    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
