package com.inholland.bank.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerIbanDTO {
    private String fullName;
    private String iban;

    public CustomerIbanDTO(String fullName, String iban) {
        this.fullName = fullName;
        this.iban = iban;
    }
}
