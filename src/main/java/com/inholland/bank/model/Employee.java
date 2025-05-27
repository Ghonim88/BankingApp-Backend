package com.inholland.bank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "userId")
public class Employee extends User {
    @PrePersist
    public void prePersist() { //set default userRole to EMPLOYEE
        // if userRole is null, set it to EMPLOYEE
        // this is to ensure that the userRole is always set to EMPLOYEE when a new employee is created
        //TODO: we already do this for customers as well, should we delete it? then the employee become just an user?
        if (userRole == null) {
            this.userRole = UserRole.EMPLOYEE;
        }
    }
  
}
