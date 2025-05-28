package com.inholland.bank.unit_testing.service;

import com.inholland.bank.repository.AccountRepository;
import com.inholland.bank.service.AccountService;
import com.inholland.bank.service.IbanService;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IbanServiceTest {

    private final IbanService ibanService = new IbanService();
    private final AccountRepository mockRepo = mock(AccountRepository.class);
    private final AccountService accountService;

    public IbanServiceTest() {
        accountService = new AccountService();
        // Manually inject mocks
        injectDependencies();
    }

    private void injectDependencies() {
        // Use reflection since fields are private
        try {
            var ibanServiceField = AccountService.class.getDeclaredField("ibanService");
            ibanServiceField.setAccessible(true);
            ibanServiceField.set(accountService, ibanService);

            var accountRepoField = AccountService.class.getDeclaredField("accountRepository");
            accountRepoField.setAccessible(true);
            accountRepoField.set(accountService, mockRepo);

            // Mock repo to always return empty (simulate unique IBANs)
            when(mockRepo.findByIban(anyString())).thenReturn(Optional.empty());
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies into AccountService", e);
        }
    }
    @Test
    void testIbanFormatAndChecksum() {
        String iban = ibanService.generateIban();

        assertTrue(iban.startsWith("NL"), "IBAN should start with 'NL'");
        assertEquals(18, iban.length(), "IBAN should be 18 characters long");

        String rearranged = iban.substring(4) + iban.substring(0, 4);
        String numeric = rearranged.chars()
                .mapToObj(c -> Character.isLetter((char) c) ? String.valueOf(c - 55) : String.valueOf((char) c))
                .reduce("", String::concat);

        int mod = new java.math.BigInteger(numeric).mod(new java.math.BigInteger("97")).intValue();
        assertEquals(1, mod, "Checksum must result in mod 97 == 1");
    }

    @Test
    void testUniqueIbans() {
        Set<String> ibans = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String iban = accountService.generateUniqueIban();
            assertFalse(ibans.contains(iban), "Duplicate IBAN found: " + iban);
            ibans.add(iban);
        }
    }
}