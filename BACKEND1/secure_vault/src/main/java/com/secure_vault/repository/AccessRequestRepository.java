package com.secure_vault.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.secure_vault.entity.AccessRequest;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    Optional<AccessRequest> findTopByAdminIdAndUserIdOrderByRequestedAtDesc(Long adminId, Long userId);

    List<AccessRequest> findByUserIdAndStatusOrderByRequestedAtDesc(Long userId, String status);

    List<AccessRequest> findByAdminIdAndStatusOrderByRequestedAtDesc(Long adminId, String status);
}
