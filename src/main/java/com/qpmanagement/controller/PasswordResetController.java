package com.qpmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpmanagement.dto.ApiResponse;
import com.qpmanagement.dto.PasswordResetRequestDTO;
import com.qpmanagement.dto.PasswordResetResponse;
import com.qpmanagement.dto.PasswordResetVerifyDTO;
import com.qpmanagement.entity.PasswordResetRequest.ResetStatus;
import com.qpmanagement.service.PasswordResetService;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Create a password reset request
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<?>> requestPasswordReset(@RequestBody PasswordResetRequestDTO requestDTO) {
        try {
            PasswordResetResponse response = passwordResetService.createResetRequest(requestDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset request submitted successfully. Please wait for admin approval.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Verify code and reset password
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyAndResetPassword(@RequestBody PasswordResetVerifyDTO verifyDTO) {
        try {
            passwordResetService.verifyAndResetPassword(verifyDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Get pending reset requests (for admin)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<PasswordResetResponse>> getPendingRequests() {
        try {
            List<PasswordResetResponse> requests = passwordResetService.getResetRequestsByStatus(ResetStatus.PENDING);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
