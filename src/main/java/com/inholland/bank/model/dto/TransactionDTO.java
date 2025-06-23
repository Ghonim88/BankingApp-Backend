package com.inholland.bank.model.dto;

import com.inholland.bank.model.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private String senderIban;
    private String receiverIban;
    private BigDecimal transactionAmount;
    private LocalDateTime createdAt;
    private TransactionType transactionType;
}
