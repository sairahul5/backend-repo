package com.qpmanagement.service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;
import com.qpmanagement.dto.AuthResponse;
import com.qpmanagement.dto.LoginRequest;
import com.qpmanagement.dto.MfaSetupResponse;
import com.qpmanagement.dto.RegisterRequest;
import com.qpmanagement.entity.User;
import com.qpmanagement.repository.UserRepository;
import com.qpmanagement.security.JwtUtil;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private MfaService mfaService;
    
    // Temporary storage for MFA verification (in production, use Redis or similar)
    private final ConcurrentHashMap<String, String> tempTokenStore = new ConcurrentHashMap<>();
    
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Auto-generate email from username
        String email = request.getUsername() + "@sairahul.adabala";
        
        // Check if email already exists (should not happen if username is unique)
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName() != null ? request.getName() : request.getUsername());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        
        userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // Check if user has MFA enabled
        if (user.isMfaEnabled()) {
            // Generate temporary token for MFA verification
            String tempToken = UUID.randomUUID().toString();
            tempTokenStore.put(tempToken, user.getUsername());
            
            // Return response indicating MFA is required
            AuthResponse response = new AuthResponse(tempToken, user.getEmail(), user.getName(), 
                                                     user.getRole().name(), true);
            return response;
        }
        
        // Normal login without MFA
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }
    
    public MfaSetupResponse setupMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            String secret = mfaService.generateSecret();
            String qrUrl = mfaService.generateQRUrl(user.getEmail(), secret, "QP Management System");
            String qrCodeImage = mfaService.generateQRCodeImage(qrUrl);
            
            // Save secret to user
            user.setMfaSecret(secret);
            userRepository.save(user);
            
            return new MfaSetupResponse(secret, qrCodeImage);
        } catch (IOException | WriterException e) {
            throw new RuntimeException("Failed to setup MFA: " + e.getMessage());
        }
    }
    
    public AuthResponse verifyMfa(String tempToken, String code) {
        System.out.println("=== MFA Verification Debug ===");
        System.out.println("Temp Token: " + tempToken);
        System.out.println("Code received: " + code);
        
        String username = tempTokenStore.get(tempToken);
        if (username == null) {
            System.out.println("ERROR: Temp token not found or expired");
            throw new RuntimeException("Invalid or expired temp token");
        }
        
        System.out.println("Username from temp token: " + username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("User found: " + user.getUsername());
        System.out.println("MFA Enabled: " + user.isMfaEnabled());
        System.out.println("MFA Secret exists: " + (user.getMfaSecret() != null));
        
        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            System.out.println("ERROR: MFA not properly enabled");
            throw new RuntimeException("MFA is not enabled for this user");
        }
        
        boolean isValid = mfaService.verifyCode(user.getMfaSecret(), code);
        System.out.println("Code verification result: " + isValid);
        
        if (!isValid) {
            System.out.println("ERROR: Invalid MFA code");
            throw new RuntimeException("Invalid MFA code");
        }
        
        // Remove temp token
        tempTokenStore.remove(tempToken);
        
        System.out.println("SUCCESS: MFA verification successful");
        
        // Generate actual JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }
    
    public void enableMfa(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getMfaSecret() == null) {
            throw new RuntimeException("MFA secret not found. Please setup MFA first.");
        }
        
        if (!mfaService.verifyCode(user.getMfaSecret(), code)) {
            throw new RuntimeException("Invalid MFA code");
        }
        
        user.setMfaEnabled(true);
        userRepository.save(user);
    }
    
    public void disableMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
    }
}
