package com.qpmanagement.dto;

public class PasswordResetAcceptDTO {
    
    private Long requestId;

    // Constructors
    public PasswordResetAcceptDTO() {
    }

    public PasswordResetAcceptDTO(Long requestId) {
        this.requestId = requestId;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
