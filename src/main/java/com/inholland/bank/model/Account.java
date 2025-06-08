package com.inholland.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

  @Column(name = "balance", nullable = false, precision = 19, scale = 4)
  private BigDecimal balance = BigDecimal.valueOf(0);

  @Column(name = "iban", unique = true, nullable = false)
  private String iban;

  @Column(name = "daily_transfer_limit", nullable = false, precision = 19, scale = 4)
  private BigDecimal dailyTransferLimit;

  @Column(name = "absolute_transfer_limit", nullable = false, precision = 19, scale = 4)
  private BigDecimal absoluteTransferLimit;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", nullable = false)
    private AccountType accountType;
  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;


}
