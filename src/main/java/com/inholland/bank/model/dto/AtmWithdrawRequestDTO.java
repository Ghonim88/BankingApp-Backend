package com.inholland.bank.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AtmWithdrawRequestDTO {
    private Long accountId;
    private BigDecimal amount;
}
