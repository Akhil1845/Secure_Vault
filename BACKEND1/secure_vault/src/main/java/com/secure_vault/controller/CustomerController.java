package com.secure_vault.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.secure_vault.dto.AccessRequestViewDto;
import com.secure_vault.dto.CustomerViewDto;
import com.secure_vault.entity.AccessRequest;
import com.secure_vault.entity.AdminUser;
import com.secure_vault.entity.Customer;
import com.secure_vault.repository.AccessRequestRepository;
import com.secure_vault.repository.AdminUserRepository;
import com.secure_vault.repository.CustomerRepository;
import com.secure_vault.service.MaskingService;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final AccessRequestRepository accessRequestRepository;
    private final AdminUserRepository adminUserRepository;
    private final MaskingService maskingService;

    public CustomerController(
            CustomerRepository customerRepository,
            AccessRequestRepository accessRequestRepository,
            AdminUserRepository adminUserRepository,
            MaskingService maskingService
    ) {
        this.customerRepository = customerRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.adminUserRepository = adminUserRepository;
        this.maskingService = maskingService;
    }

    @GetMapping
    public List<CustomerViewDto> getCustomers(
            @RequestParam(required = false, defaultValue = "USER") String role,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long adminId
    ) {
        if ("USER".equalsIgnoreCase(role)) {
            if (userId == null) {
                return new ArrayList<>();
            }
            return customerRepository.findByIdAndRole(userId, "USER")
                    .map(customer -> List.of(maskingService.applyMask(customer, "USER")))
                    .orElseGet(ArrayList::new);
        }

        if (!"ADMIN".equalsIgnoreCase(role) || adminId == null) {
            return new ArrayList<>();
        }

        Set<Long> approvedIds = accessRequestRepository.findByAdminIdAndStatusOrderByRequestedAtDesc(adminId, "APPROVED")
                .stream()
                .map(AccessRequest::getUserId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (approvedIds.isEmpty()) {
            return new ArrayList<>();
        }

        return customerRepository.findByIdInAndRoleOrderByIdAsc(new ArrayList<>(approvedIds), "USER")
                .stream()
                .map(customer -> maskingService.applyMask(customer, "ADMIN"))
                .toList();
    }

    @GetMapping("/users-basic")
    public List<Map<String, Object>> getBasicUsers(@RequestParam Long adminId) {
        List<Customer> users = customerRepository.findByRoleOrderByIdAsc("USER");
        List<Map<String, Object>> result = new ArrayList<>();

        for (Customer user : users) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", user.getId());
            row.put("name", user.getName());
            row.put("username", user.getUsername());
                row.put("email", user.getEmail());
            String status = accessRequestRepository.findTopByAdminIdAndUserIdOrderByRequestedAtDesc(adminId, user.getId())
                    .map(AccessRequest::getStatus)
                    .orElse("NONE");
            row.put("requestStatus", status);
            result.add(row);
        }
        return result;
    }

    @PostMapping("/access-request")
    public Map<String, Object> createAccessRequest(@RequestBody Map<String, Object> payload) {
        Long adminId = toLong(payload.get("adminId"));
        String username = toText(payload.get("username"));
        String email = toText(payload.get("email"));
        Map<String, Object> response = new HashMap<>();

        if (adminId == null) {
            response.put("status", "error");
            response.put("message", "adminId is required");
            return response;
        }

        if (adminUserRepository.findById(adminId).isEmpty()) {
            response.put("status", "error");
            response.put("message", "Invalid admin identity");
            return response;
        }

        if (username.isBlank() || email.isBlank()) {
            response.put("status", "error");
            response.put("message", "Both username and email are required");
            return response;
        }

        Optional<Customer> targetUserOptional = customerRepository.findByUsernameAndRole(username, "USER");

        if (targetUserOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No user found for provided username");
            return response;
        }

        Customer targetUser = targetUserOptional.get();
        if (!email.equalsIgnoreCase(targetUser.getEmail())) {
            response.put("status", "error");
            response.put("message", "Username and email do not match");
            return response;
        }

        Optional<AccessRequest> lastRequest = accessRequestRepository.findTopByAdminIdAndUserIdOrderByRequestedAtDesc(adminId, targetUser.getId());
        if (lastRequest.isPresent() && "PENDING".equalsIgnoreCase(lastRequest.get().getStatus())) {
            response.put("status", "error");
            response.put("message", "Request is already pending for this user");
            return response;
        }

        AccessRequest request = new AccessRequest();
        request.setAdminId(adminId);
        request.setUserId(targetUser.getId());
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());
        accessRequestRepository.save(request);

        response.put("status", "success");
        response.put("message", "Access request sent to @" + targetUser.getUsername());
        return response;
    }

    @GetMapping("/requests")
    public List<AccessRequestViewDto> getUserRequests(@RequestParam Long userId) {
        List<AccessRequest> pendingRequests = accessRequestRepository.findByUserIdAndStatusOrderByRequestedAtDesc(userId, "PENDING");
        return pendingRequests.stream().map(request -> {
            AdminUser admin = adminUserRepository.findById(request.getAdminId()).orElse(null);
            return new AccessRequestViewDto(
                    request.getId(),
                    request.getAdminId(),
                    admin != null ? admin.getCompanyName() : "Unknown Organization",
                    admin != null ? admin.getCompanyEmail() : "unknown@company",
                    request.getRequestedAt()
            );
        }).toList();
    }

    @PostMapping("/add")
    public Map<String, Object> addCustomerData(@RequestBody Map<String, Object> payload) {
        Long userId = toLong(payload.get("userId"));
        String docName = toText(payload.get("docName"));
        String docNumber = toText(payload.get("docNumber"));
        Map<String, Object> response = new HashMap<>();

        if (userId == null) {
            response.put("status", "error");
            response.put("message", "userId is required");
            return response;
        }

        if (docName.isBlank() || docNumber.isBlank()) {
            response.put("status", "error");
            response.put("message", "Document name and number are required");
            return response;
        }

        Optional<Customer> customerOptional = customerRepository.findById(userId);
        if (customerOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Customer not found");
            return response;
        }

        Customer customer = customerOptional.get();
        String docNameLower = docName.toLowerCase();

        // Store the document type
        customer.setDocumentType(docName);

        // Determine which field to update based on document name
        if (docNameLower.contains("bank")) {
            customer.setBankAccount(docNumber);
        } else if (docNameLower.contains("aadhaar") || docNameLower.contains("aadhar")) {
            customer.setAadhaar(docNumber);
        } else {
            // For other documents, store in aadhaar field with doc type prefix
            customer.setAadhaar(docName + ": " + docNumber);
        }

        customerRepository.save(customer);

        response.put("status", "success");
        response.put("message", "Customer data saved successfully");
        return response;
    }

    @PostMapping("/requests/{requestId}/respond")
    public Map<String, Object> respondToAccessRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> payload
    ) {
        Map<String, Object> response = new HashMap<>();
        Long userId;
        try {
            userId = Long.valueOf(payload.getOrDefault("userId", "0"));
        } catch (NumberFormatException ex) {
            response.put("status", "error");
            response.put("message", "Invalid userId");
            return response;
        }
        String action = payload.getOrDefault("action", "REJECT").trim().toUpperCase();

        Optional<AccessRequest> requestOptional = accessRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Request not found");
            return response;
        }

        AccessRequest request = requestOptional.get();
        if (!request.getUserId().equals(userId)) {
            response.put("status", "error");
            response.put("message", "You cannot respond to this request");
            return response;
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            response.put("status", "error");
            response.put("message", "Request is already resolved");
            return response;
        }

        request.setStatus("ACCEPT".equals(action) ? "APPROVED" : "REJECTED");
        request.setRespondedAt(LocalDateTime.now());
        accessRequestRepository.save(request);

        response.put("status", "success");
        response.put("message", "Request updated successfully");
        return response;
    }

    private String toText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
