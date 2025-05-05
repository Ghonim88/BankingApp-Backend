package com.inholland.bank.model.dto;

import com.inholland.bank.model.AccountStatus;
import com.inholland.bank.model.AccountType;
import com.inholland.bank.model.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends UserDTO{
  @NotBlank
  private String phoneNumber;
  @NotBlank
  private String bsn;
  @NotNull
  private AccountType accountType;
  @NotNull
  private double transactionLimit;
  @NotNull
  private AccountStatus accountStatus;
}
