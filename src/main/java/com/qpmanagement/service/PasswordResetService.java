package com.qpmanagement.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qpmanagement.dto.PasswordResetRequestDTO;
import com.qpmanagement.dto.PasswordResetResponse;
import com.qpmanagement.dto.PasswordResetVerifyDTO;
import com.qpmanagement.entity.PasswordResetRequest;
import com.qpmanagement.entity.PasswordResetRequest.ResetStatus;
import com.qpmanagement.entity.User;
import com.qpmanagement.repository.PasswordResetRequestRepository;
import com.qpmanagement.repository.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRequestRepository resetRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    /**
     * Create a password reset request
     */
    public PasswordResetResponse createResetRequest(PasswordResetRequestDTO requestDTO) {
        // Verify user exists
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a pending request
        Optional<PasswordResetRequest> existingRequest = 
                resetRequestRepository.findByUsernameAndStatus(requestDTO.getUsername(), ResetStatus.PENDING);
        
        if (existingRequest.isPresent()) {
            throw new RuntimeException("You already have a pending password reset request");
        }

        // Create new reset request
        PasswordResetRequest resetRequest = new PasswordResetRequest(user.getUsername(), user.getEmail());
        resetRequest = resetRequestRepository.save(resetRequest);

        return mapToResponse(resetRequest);
    }

    /**
     * Accept a reset request and generate a 6-digit code
     */
    @Transactional
    public PasswordResetResponse acceptResetRequest(Long requestId) {
        PasswordResetRequest resetRequest = resetRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Reset request not found"));

        if (resetRequest.getStatus() != ResetStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }

        // Generate a unique 6-digit code
        String code = generateUniqueCode();
        resetRequest.setCode(code);
        resetRequest = resetRequestRepository.save(resetRequest);

        // Send email with code
        emailService.sendPasswordResetCode(resetRequest.getEmail(), resetRequest.getUsername(), code);

        return mapToResponse(resetRequest);
    }

    /**
     * Verify code and reset password
     */
    @Transactional
    public void verifyAndResetPassword(PasswordResetVerifyDTO verifyDTO) {
        // Find pending request with matching username and code
        PasswordResetRequest resetRequest = resetRequestRepository
                .findByUsernameAndCodeAndStatus(verifyDTO.getUsername(), verifyDTO.getCode(), ResetStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Invalid username or code"));

        // Get user
        User user = userRepository.findByUsername(verifyDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(verifyDTO.getNewPassword()));
        userRepository.save(user);

        // Mark request as completed
        resetRequest.setStatus(ResetStatus.COMPLETED);
        resetRequest.setCompletedAt(LocalDateTime.now());
        resetRequestRepository.save(resetRequest);
    }

    /**
     * Get all reset requests
     */
    public List<PasswordResetResponse> getAllResetRequests() {
        return resetRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reset requests by status
     */
    public List<PasswordResetResponse> getResetRequestsByStatus(ResetStatus status) {
        return resetRequestRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete completed requests older than 24 hours
     */
    @Transactional
    public void cleanupOldRequests() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        resetRequestRepository.deleteByStatusAndCompletedAtBefore(ResetStatus.COMPLETED, cutoffTime);
    }

    /**
     * Generate a unique 6-digit code
     */
    private String generateUniqueCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1000000));
        } while (resetRequestRepository.findByCode(code).isPresent());
        return code;
    }

    /**
     * Map entity to response DTO
     */
    private PasswordResetResponse mapToResponse(PasswordResetRequest request) {
        return new PasswordResetResponse(
                request.getId(),
                request.getUsername(),
                request.getEmail(),
                request.getCode(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getCompletedAt()
        );
    }
}
