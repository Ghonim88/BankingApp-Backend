package com.inholland.bank.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
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
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
public class Customer extends User{

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "BSN",unique = true, nullable = false)
    private String bsn;

    @Enumerated(EnumType.STRING)
    @Column(name = "accountStatus", nullable = false)
    private AccountStatus accountStatus;

    // One-to-many relationship with Account
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Account> accounts;

    @PrePersist
    public void prePersist() { //set default userRole to CUSTOMER
        // if userRole is null, set it to CUSTOMER
        // this is to ensure that the userRole is always set to CUSTOMER when a new customer is created
        // this is also in employee, i believe one of the 2 should be removed
        if (userRole == null) {
            this.userRole = UserRole.CUSTOMER;
        }
        // Set default AccountType to CHECKING if not provided
        //TODO: i don't think it s necessary, the admin will always add the account type, if group agrees, delete


        if(accountStatus == null){
            this.accountStatus = AccountStatus.Pending; // Set default account status to PENDING waiting for employee approval
        }



    }

  }
