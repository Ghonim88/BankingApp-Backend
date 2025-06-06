package com.inholland.bank.model.dto;

import com.inholland.bank.model.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String iban;
    private BigDecimal balance;
    private BigDecimal dailyTransferLimit;
    private BigDecimal absoluteTransferLimit;
    private AccountType accountType;
    private Long customerId;
    private String ownerEmail;
}
