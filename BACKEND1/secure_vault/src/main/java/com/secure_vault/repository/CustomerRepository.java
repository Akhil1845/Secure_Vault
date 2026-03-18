package com.secure_vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.secure_vault.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
