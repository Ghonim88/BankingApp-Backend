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
  private BigDecimal balance;

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
  // Constructor with account type and customer
  // Constructor with account type and customer
  public Account(AccountType accountType, Customer customer) {
    this.accountType = accountType;
    this.customer = customer;
    this.iban = generateIBAN();  // Generate unique IBAN
    this.balance = BigDecimal.ZERO;                  // Set initial balance to 0
    this.dailyTransferLimit = BigDecimal.valueOf(5000);   // Default daily transfer limit
    this.absoluteTransferLimit = BigDecimal.valueOf(10000); // Default absolute transfer limit
  }

  // Method to generate a unique IBAN for each account
  private String generateIBAN() {
    // Example: "NL" for the Netherlands
    String countryCode = "NL";

    // Generate a random BBAN (Basic Bank Account Number)
    String bban = UUID.randomUUID().toString().replace("-", "").substring(0, 10); // A 10-digit number

    // The checksum can be calculated based on the country code and BBAN. For simplicity, here we will generate it as a random value.
    String checksum = String.format("%02d", (int) (Math.random() * 99));  // Generate a two-digit checksum (00-99)

    // Concatenate the country code, checksum, and BBAN to form the IBAN
    return countryCode + checksum + bban;
  }

  // Constructors, getters, and setters

}
