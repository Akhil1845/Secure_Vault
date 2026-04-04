package com.secure_vault.service;

import org.springframework.stereotype.Service;

import com.secure_vault.dto.CustomerViewDto;
import com.secure_vault.entity.Customer;

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
        return "XXXX-XXXX-" + bankAccount.substring(bankAccount.length() - 4);
    }

    public CustomerViewDto applyMask(Customer customer, String role) {
        boolean admin = "ADMIN".equalsIgnoreCase(role);
        return new CustomerViewDto(
                customer.getId(),
                customer.getName(),
                admin ? maskAadhaar(customer.getAadhaar()) : customer.getAadhaar(),
                admin ? maskBank(customer.getBankAccount()) : customer.getBankAccount(),
                customer.getUsername(),
                customer.getDocumentType()
        );
    }
}
