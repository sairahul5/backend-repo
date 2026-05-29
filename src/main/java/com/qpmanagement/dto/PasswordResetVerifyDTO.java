package com.qpmanagement.dto;

public class PasswordResetVerifyDTO {
    
    private String username;
    private String code;
    private String newPassword;

    // Constructors
    public PasswordResetVerifyDTO() {
    }

    public PasswordResetVerifyDTO(String username, String code, String newPassword) {
        this.username = username;
        this.code = code;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
