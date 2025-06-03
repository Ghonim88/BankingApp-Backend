package com.inholland.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


  @Column(name = "balance", nullable = false)
  private double balance = 0.0;

  @Column(name = "iban", unique = true, nullable = false)
  private String iban;

  @Column(name = "daily_transfer_limit", nullable = false)
  private int dailyTransferLimit = 5000;

  @Column(name = "absolute_transfer_limit", nullable = false)
  private int absoluteTransferLimit = 10000;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", nullable = false)
    private AccountType accountType;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

}
