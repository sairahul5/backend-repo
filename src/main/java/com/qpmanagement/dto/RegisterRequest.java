package com.qpmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    
    public RegisterRequest() {
    }
    
    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 39, message = "Username must be between 5 and 39 characters")
    @Pattern(regexp = "^[a-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$", 
             message = "Username must contain only lowercase letters, optional numbers, and optional symbols")
    private String username;
    
    private String name;
    
    private String email; // Will be auto-generated from username
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).+$", 
             message = "Password must contain letters, numbers, and symbols")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
