package com.secure_vault.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.secure_vault.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByUsername(String username);

	Optional<Customer> findByUsernameAndRole(String username, String role);

	Optional<Customer> findByEmailAndRole(String email, String role);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<Customer> findByIdAndRole(Long id, String role);

	List<Customer> findByRoleOrderByIdAsc(String role);

	List<Customer> findByIdInAndRoleOrderByIdAsc(List<Long> ids, String role);
}
