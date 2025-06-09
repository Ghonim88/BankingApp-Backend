package com.inholland.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JoinColumn(name = "from_account_id", nullable = false)
    @JsonIgnore
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = false)
    @JsonIgnore
    private Account toAccount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal transactionAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
