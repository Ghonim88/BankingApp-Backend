package com.inholland.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String senderIban;

    @Column(nullable = false)
    private String receiverIban;

    @Column(nullable = false)
    private BigDecimal transactionAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
