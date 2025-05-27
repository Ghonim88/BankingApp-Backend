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
  private double balance;
  //TODO: do we really need currency? everything should be in euros anyway, so i don t see the point in having this
  private String currency;

  @Column(name = "iban", unique = true, nullable = false)
  private String iban;

    private int dailyTransferLimit;
    private int absoluteTransferLimit;

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
    this.balance = 0.0;  // Set initial balance to 0
    this.currency = "EUR"; // Set default currency to Euro
    this.dailyTransferLimit = 5000; // Default daily transfer limit
    this.absoluteTransferLimit = 10000;
  }

  // Method to generate a unique IBAN for each account
  private String generateIBAN() {
    // Example: "NL" for the Netherlands
    String countryCode = "NL";

    // Generate a random IBAN (Basic Bank Account Number)
    String iban = UUID.randomUUID().toString().replace("-", "").substring(0, 10); // A 10-digit number

    // The checksum can be calculated based on the country code and BBAN. For simplicity, here we will generate it as a random value.
    String checksum = String.format("%02d", (int) (Math.random() * 99));  // Generate a two-digit checksum (00-99)

    // Concatenate the country code, checksum, and BBAN to form the IBAN
    return countryCode + checksum + iban;
  }

  // Constructors, getters, and setters

}
