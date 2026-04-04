package com.secure_vault.dto;

import java.time.LocalDateTime;

public class AccessRequestViewDto {
    private Long requestId;
    private Long adminId;
    private String companyName;
    private String companyEmail;
    private LocalDateTime requestedAt;

    public AccessRequestViewDto() {
    }

    public AccessRequestViewDto(Long requestId, Long adminId, String companyName, String companyEmail, LocalDateTime requestedAt) {
        this.requestId = requestId;
        this.adminId = adminId;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.requestedAt = requestedAt;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
