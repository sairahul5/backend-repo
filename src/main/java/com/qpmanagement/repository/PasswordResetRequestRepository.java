package com.qpmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qpmanagement.entity.PasswordResetRequest;
import com.qpmanagement.entity.PasswordResetRequest.ResetStatus;

@Repository
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    
    Optional<PasswordResetRequest> findByUsernameAndStatus(String username, ResetStatus status);
    
    Optional<PasswordResetRequest> findByUsernameAndCodeAndStatus(String username, String code, ResetStatus status);
    
    Optional<PasswordResetRequest> findByCode(String code);
    
    List<PasswordResetRequest> findAllByOrderByCreatedAtDesc();
    
    List<PasswordResetRequest> findByStatusOrderByCreatedAtDesc(ResetStatus status);
    
    List<PasswordResetRequest> findByStatusAndCompletedAtBefore(ResetStatus status, LocalDateTime cutoffTime);
    
    void deleteByStatusAndCompletedAtBefore(ResetStatus status, LocalDateTime cutoffTime);
}
