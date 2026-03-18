package com.secure_vault.service;

import org.springframework.stereotype.Service;

@Service
public class MaskingService {

    public String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) {
            return aadhaar;
        }
        return "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
    }

    public String maskBank(String bankAccount) {
        if (bankAccount == null || bankAccount.length() < 4) {
            return bankAccount;
        }
        return "XXXX" + bankAccount.substring(bankAccount.length() - 4);
    }
}
