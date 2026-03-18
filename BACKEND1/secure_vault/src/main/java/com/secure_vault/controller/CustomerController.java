package com.secure_vault.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.secure_vault.entity.Customer;
import com.secure_vault.repository.CustomerRepository;
import com.secure_vault.service.MaskingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final MaskingService maskingService;

    public CustomerController(CustomerRepository customerRepository, MaskingService maskingService) {
        this.customerRepository = customerRepository;
        this.maskingService = maskingService;
    }

    @GetMapping
    public List<Customer> getCustomers(@RequestParam(required = false, defaultValue = "USER") String role) {
        List<Customer> customers = customerRepository.findAll();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return customers.stream().map(customer -> {
                Customer maskedCustomer = new Customer(
                        customer.getId(),
                        customer.getName(),
                        maskingService.maskAadhaar(customer.getAadhaar()),
                        maskingService.maskBank(customer.getBankAccount())
                );
                return maskedCustomer;
            }).collect(Collectors.toList());
        }

        return customers;
    }
}
