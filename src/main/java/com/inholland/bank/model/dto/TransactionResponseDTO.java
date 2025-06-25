package com.inholland.bank.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private String fromIban;
    private String fromAccountHolderName;

    private String toIban;
    private String toAccountHolderName;

    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String transactionType;

    private String initiatorName;
    private String initiatorRole;
}
