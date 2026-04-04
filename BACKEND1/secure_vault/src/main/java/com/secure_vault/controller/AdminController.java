package com.secure_vault.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure_vault.entity.AdminUser;
import com.secure_vault.entity.AuditLog;
import com.secure_vault.repository.AdminUserRepository;
import com.secure_vault.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminUserRepository adminUserRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminController(AdminUserRepository adminUserRepository, AuditLogRepository auditLogRepository) {
        this.adminUserRepository = adminUserRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(
            @RequestBody Map<String, String> payload,
            HttpServletRequest request
    ) {
        String email = payload.getOrDefault("email", "").trim();
        String password = payload.getOrDefault("password", "").trim();
        String secretKey = payload.getOrDefault("secretKey", "").trim();
        String ipAddress = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();

        Map<String, Object> response = new HashMap<>();
        if (email.isEmpty() || password.isEmpty() || secretKey.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email, password, and secret key are required");
            return ResponseEntity.badRequest().body(response);
        }

        AdminUser admin = adminUserRepository.findByCompanyEmail(email).orElse(null);
        if (admin == null || !password.equals(admin.getPassword()) || !secretKey.equals(admin.getSecretKey())) {
            long failedAttempts = auditLogRepository.countFailedByIp(ipAddress) + 1;
            createAuditLog(email, ipAddress, failedAttempts >= 3 ? "FAILED_FLAGGED" : "FAILED");

            response.put("status", "error");
            response.put("message", "Invalid admin credentials or secret key");
            response.put("failedAttemptsFromIp", failedAttempts);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        createAuditLog(email, ipAddress, "SUCCESS");
        response.put("status", "success");
        response.put("message", "Admin login successful");
        response.put("role", "ADMIN");
        response.put("adminId", admin.getId());
        response.put("username", admin.getCompanyEmail());
        response.put("companyName", admin.getCompanyName());

        return ResponseEntity.ok(response);
    }

    private void createAuditLog(String email, String ipAddress, String status) {
        AuditLog log = new AuditLog();
        log.setAttemptedEmail(email);
        log.setIpAddress(ipAddress);
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
