package com.inholland.bank.config;

import com.inholland.bank.service.IbanService;

public class IbanTestRunner {
    public static void main(String[] args) {
        IbanService ibanService = new IbanService();

        System.out.println("Generated IBANs:");
        for (int i = 0; i < 10; i++) {
            System.out.println(ibanService.generateIban());
        }
    }
}
