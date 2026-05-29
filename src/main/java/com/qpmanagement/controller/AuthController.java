package com.qpmanagement.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpmanagement.dto.ApiResponse;
import com.qpmanagement.dto.AuthResponse;
import com.qpmanagement.dto.LoginRequest;
import com.qpmanagement.dto.MfaSetupResponse;
import com.qpmanagement.dto.MfaVerifyRequest;
import com.qpmanagement.dto.RegisterRequest;
import com.qpmanagement.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Registration successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/mfa/setup")
    public ResponseEntity<ApiResponse<MfaSetupResponse>> setupMfa(Authentication authentication) {
        try {
            String username = authentication.getName();
            MfaSetupResponse response = authService.setupMfa(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "MFA setup initiated", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/mfa/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyMfa(@RequestBody MfaVerifyRequest request) {
        try {
            AuthResponse response = authService.verifyMfa(request.getTempToken(), request.getCode());
            return ResponseEntity.ok(new ApiResponse<>(true, "MFA verification successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/mfa/enable")
    public ResponseEntity<ApiResponse<String>> enableMfa(Authentication authentication, 
                                                          @RequestBody Map<String, String> request) {
        try {
            String username = authentication.getName();
            String code = request.get("code");
            authService.enableMfa(username, code);
            return ResponseEntity.ok(new ApiResponse<>(true, "MFA enabled successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/mfa/disable")
    public ResponseEntity<ApiResponse<String>> disableMfa(Authentication authentication) {
        try {
            String username = authentication.getName();
            authService.disableMfa(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "MFA disabled successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
