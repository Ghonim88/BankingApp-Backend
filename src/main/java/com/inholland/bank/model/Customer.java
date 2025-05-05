package com.inholland.bank.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "userId")
public class Customer extends User{

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "BSN",unique = true, nullable = false)
    private String bsn;

    @Enumerated(EnumType.STRING)

    @Column(name = "AccountType", nullable = false)
    private AccountType accountType;

    @Column(name = "transactionLimit", nullable = false)
    private double transactionLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "accountStatus", nullable = false)
    private AccountStatus accountStatus;

    // One-to-many relationship with Account
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;

    @PrePersist
    public void prePersist() { //set default userRole to CUSTOMER TODO: CHECK WITH DANIEL ABOUT THIS PROCESS
        // if userRole is null, set it to CUSTOMER
        // this is to ensure that the userRole is always set to CUSTOMER when a new customer is created
        if (userRole == null) {
            this.userRole = UserRole.CUSTOMER;
        }
        // Set default AccountType to CHECKING if not provided
        if (accountType == null) {
            this.accountType = AccountType.CHECKING;
        }
        // Set default transactionLimit to 1000 if not provided
        if (transactionLimit == 0) {
            this.transactionLimit = 1000;
        }
        if(accountStatus == null){
            this.accountStatus = AccountStatus.Pending; // Set default account status to PENDING waiting for employee approval
        }



    }

  }
