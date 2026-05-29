package com.qpmanagement.dto;

public class MfaVerifyRequest {
    private String email;
    private String code;
    private String tempToken;
    
    public MfaVerifyRequest() {
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
    
    public String getTempToken() {
        return tempToken;
    }
    
    public void setTempToken(String tempToken) {
        this.tempToken = tempToken;
    }
}
