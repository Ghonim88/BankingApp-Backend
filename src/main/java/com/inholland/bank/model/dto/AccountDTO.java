package com.inholland.bank.model.dto;

import com.inholland.bank.model.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String iban;
    private double balance;
    private int dailyTransferLimit;
    private int absoluteTransferLimit;
    private AccountType accountType;
    private Long customerId;
}
