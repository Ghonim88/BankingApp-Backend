package com.inholland.bank.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "userId")
public class Customer extends User{

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "BSN", nullable = false)
    private String bsn;

    @Enumerated(EnumType.STRING)

    @Column(name = "AccountType", nullable = false)
    private AccountType accounType; 

    @Column(name = "transactionLimit", nullable = false)
    private double transactionLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "userRole", nullable = false)
    private UserRole userRole; 

    @PrePersist
    public void prePersist() { //set default userRole to CUSTOMER
        // if userRole is null, set it to CUSTOMER
        // this is to ensure that the userRole is always set to CUSTOMER when a new customer is created
        if (userRole == null) {
            this.userRole = UserRole.CUSTOMER;
        }
    }

  }
