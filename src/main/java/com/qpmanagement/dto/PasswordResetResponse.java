package com.qpmanagement.dto;

import java.time.LocalDateTime;

import com.qpmanagement.entity.PasswordResetRequest.ResetStatus;

public class PasswordResetResponse {
    
    private Long id;
    private String username;
    private String email;
    private String code;
    private ResetStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Constructors
    public PasswordResetResponse() {
    }

    public PasswordResetResponse(Long id, String username, String email, String code, 
                                  ResetStatus status, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ResetStatus getStatus() {
        return status;
    }

    public void setStatus(ResetStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
