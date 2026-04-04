package com.secure_vault.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.secure_vault.entity.Customer;
import com.secure_vault.entity.User;
import com.secure_vault.repository.CustomerRepository;
import com.secure_vault.repository.UserRepository;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AuthController(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> loginJson(@RequestBody Map<String, String> payload) {
        return processLogin(payload);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> loginForm(@RequestParam Map<String, String> formPayload) {
        return processLogin(formPayload);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> registerJson(@RequestBody Map<String, String> payload) {
        return processRegister(payload);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> registerForm(@RequestParam Map<String, String> formPayload) {
        return processRegister(formPayload);
    }

    private ResponseEntity<Map<String, Object>> processLogin(Map<String, String> payload) {
        String identifier = readValue(payload, "username");
        String password = readValue(payload, "password");
        Map<String, Object> response = new HashMap<>();

        if (identifier.isEmpty() || password.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        Customer customerUser = customerRepository.findByUsernameAndRole(identifier, "USER").orElse(null);
        if (customerUser == null) {
            customerUser = customerRepository.findByEmailAndRole(identifier, "USER").orElse(null);
        }

        if (customerUser != null && password.equals(customerUser.getPassword())) {
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("role", "USER");
            response.put("userId", customerUser.getId());
            response.put("username", customerUser.getUsername());
            return ResponseEntity.ok(response);
        }

        Optional<User> legacyUserOptional = userRepository.findByUsernameOrEmail(identifier, identifier);
        if (legacyUserOptional.isPresent()) {
            User legacyUser = legacyUserOptional.get();
            if ("USER".equalsIgnoreCase(legacyUser.getRole()) && password.equals(legacyUser.getPassword())) {
                Customer mappedCustomer = customerRepository.findByUsernameAndRole(legacyUser.getUsername(), "USER").orElse(null);
                if (mappedCustomer == null && legacyUser.getEmail() != null && !legacyUser.getEmail().isEmpty()) {
                    mappedCustomer = customerRepository.findByEmailAndRole(legacyUser.getEmail(), "USER").orElse(null);
                }
                if (mappedCustomer == null) {
                    mappedCustomer = new Customer();
                    mappedCustomer.setName(legacyUser.getUsername());
                    mappedCustomer.setUsername(legacyUser.getUsername());
                    mappedCustomer.setEmail(legacyUser.getEmail() == null || legacyUser.getEmail().isEmpty()
                            ? legacyUser.getUsername() + "@securevault.local"
                            : legacyUser.getEmail());
                    mappedCustomer.setPassword(legacyUser.getPassword());
                    mappedCustomer.setRole("USER");
                    mappedCustomer.setAadhaar(generatePseudoDigits(12));
                    mappedCustomer.setBankAccount(generatePseudoDigits(12));
                    mappedCustomer = customerRepository.save(mappedCustomer);
                }

                response.put("status", "success");
                response.put("message", "Login successful");
                response.put("role", "USER");
                response.put("userId", mappedCustomer.getId());
                response.put("username", mappedCustomer.getUsername());
                return ResponseEntity.ok(response);
            }
        }

            response.put("status", "error");
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    private ResponseEntity<Map<String, Object>> processRegister(Map<String, String> payload) {
        String fullName = readValue(payload, "fullName");
        String username = readValue(payload, "username");
        String email = readValue(payload, "email");
        String password = readValue(payload, "password");

        Map<String, Object> response = new HashMap<>();

        if (fullName.isEmpty()) {
            fullName = username;
        }

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.put("status", "error");
            response.put("message", "All registration fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (customerRepository.existsByUsername(username)) {
            response.put("status", "error");
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (customerRepository.existsByEmail(email)) {
            response.put("status", "error");
            response.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        Customer customer = new Customer();
        customer.setName(fullName);
        customer.setUsername(username);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setRole("USER");
        customer.setAadhaar(generatePseudoDigits(12));
        customer.setBankAccount(generatePseudoDigits(12));
        customerRepository.save(customer);

        response.put("status", "success");
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    private String readValue(Map<String, String> payload, String key) {
        if (payload != null && payload.containsKey(key) && payload.get(key) != null) {
            return payload.get(key).trim();
        }
        return "";
    }

    private String generatePseudoDigits(int length) {
        String seed = String.valueOf(System.currentTimeMillis())
                + String.valueOf(System.nanoTime())
                + ThreadLocalRandom.current().nextInt(1000, 9999);
        StringBuilder digits = new StringBuilder();
        while (digits.length() < length) {
            digits.append(seed);
        }
        return digits.substring(0, length);
    }
}
