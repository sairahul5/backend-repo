package com.qpmanagement.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private String role;
    private boolean mfaRequired;
    private String tempToken;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String name, String role) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.mfaRequired = false;
    }
    
    public AuthResponse(String tempToken, String email, String name, String role, boolean mfaRequired) {
        this.tempToken = tempToken;
        this.email = email;
        this.name = name;
        this.role = role;
        this.mfaRequired = mfaRequired;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isMfaRequired() {
        return mfaRequired;
    }
    
    public void setMfaRequired(boolean mfaRequired) {
        this.mfaRequired = mfaRequired;
    }
    
    public String getTempToken() {
        return tempToken;
    }
    
    public void setTempToken(String tempToken) {
        this.tempToken = tempToken;
    }
}
