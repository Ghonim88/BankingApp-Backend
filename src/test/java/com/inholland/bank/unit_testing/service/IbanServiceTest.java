package com.inholland.bank.unit_testing.service;

import com.inholland.bank.service.IbanService;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class IbanServiceTest {

    private final IbanService ibanService = new IbanService();

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
    void testIbanUniqueness() {
        Set<String> ibans = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            String iban = ibanService.generateIban();
            assertFalse(ibans.contains(iban), "Duplicate IBAN found!");
            ibans.add(iban);
        }
    }
}