package com.inholland.bank.unit_testing.service;


import com.inholland.bank.exceptions.InvalidAccountCreationRequestException;
import com.inholland.bank.model.Account;
import com.inholland.bank.model.AccountType;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.dto.AccountDTO;
import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.repository.CustomerRepository;
import com.inholland.bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void shouldThrowExceptionWhenNotExactlyTwoAccounts() {
        List<AccountDTO> dtos = List.of(new AccountDTO()); // only 1

        InvalidAccountCreationRequestException exception = assertThrows(
                InvalidAccountCreationRequestException.class,
                () -> accountService.createAccounts(dtos)
        );

        assertEquals("You must create exactly two accounts: one CHECKING and one SAVINGS.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotBothAccountTypesPresent() {
        AccountDTO dto1 = new AccountDTO();
        dto1.setAccountType(AccountType.CHECKING);
        AccountDTO dto2 = new AccountDTO();
        dto2.setAccountType(AccountType.CHECKING);

        List<AccountDTO> dtos = List.of(dto1, dto2);

        InvalidAccountCreationRequestException exception = assertThrows(
                InvalidAccountCreationRequestException.class,
                () -> accountService.createAccounts(dtos)
        );

        assertEquals("Both CHECKING and SAVINGS accounts are required.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdsAreDifferent() {
        AccountDTO dto1 = new AccountDTO();
        dto1.setAccountType(AccountType.CHECKING);
        dto1.setCustomerId(10L);

        AccountDTO dto2 = new AccountDTO();
        dto2.setAccountType(AccountType.SAVINGS);
        dto2.setCustomerId(11L); // Different customer ID

        List<AccountDTO> dtos = List.of(dto1, dto2);

        InvalidAccountCreationRequestException exception = assertThrows(
                InvalidAccountCreationRequestException.class,
                () -> accountService.createAccounts(dtos)
        );

        assertEquals("All accounts must belong to the same customer.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyHasAccounts() {
        // Arrange: use an existing customer ID that is known to have accounts
        Long existingCustomerId = 1L;

        AccountDTO dto1 = new AccountDTO();
        dto1.setAccountType(AccountType.CHECKING);
        dto1.setCustomerId(existingCustomerId);

        AccountDTO dto2 = new AccountDTO();
        dto2.setAccountType(AccountType.SAVINGS);
        dto2.setCustomerId(existingCustomerId);

        // Mock an existing customer with accounts
        Customer customerWithAccounts = mock(Customer.class);
        when(customerWithAccounts.getAccounts()).thenReturn(List.of(new Account()));
        when(customerRepository.findById(existingCustomerId)).thenReturn(Optional.of(customerWithAccounts));

        // Act & Assert
        InvalidAccountCreationRequestException exception = assertThrows(
                InvalidAccountCreationRequestException.class,
                () -> accountService.createAccounts(List.of(dto1, dto2))
        );

        assertEquals("Customer already has accounts. Cannot create more than one CHECKING and one SAVINGS account.", exception.getMessage());
    }
}
