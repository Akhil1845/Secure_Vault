package com.secure_vault.dto;

public class CustomerViewDto {
    private Long id;
    private String name;
    private String aadhaar;
    private String bankAccount;
    private String username;
    private String documentType;

    public CustomerViewDto() {
    }

    public CustomerViewDto(Long id, String name, String aadhaar, String bankAccount, String username) {
        this.id = id;
        this.name = name;
        this.aadhaar = aadhaar;
        this.bankAccount = bankAccount;
        this.username = username;
    }

    public CustomerViewDto(Long id, String name, String aadhaar, String bankAccount, String username, String documentType) {
        this.id = id;
        this.name = name;
        this.aadhaar = aadhaar;
        this.bankAccount = bankAccount;
        this.username = username;
        this.documentType = documentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
