package com.inholland.bank.model;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String senderIban;

    @Column(nullable = false)
    private String receiverIban;

    @Column(nullable = false)
    private double transactionAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
