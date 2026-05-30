package com.qpmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendPasswordResetCode(String toEmail, String username, String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = Map.of(
                "sender", Map.of("name", "QP Management System", "email", "noreply@qpmanagement.com"),
                "to", List.of(Map.of("email", toEmail, "name", username)),
                "subject", "Password Reset Code - QP Management System",
                "textContent", buildPasswordResetEmail(username, code)
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.brevo.com/v3/smtp/email", request, String.class
            );

            System.out.println("Email sent successfully to: " + toEmail + " | Status: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }

    private String buildPasswordResetEmail(String username, String code) {
        return String.format(
            "Dear %s,\n\n" +
            "Your password reset request has been approved.\n\n" +
            "Your password reset code is: %s\n\n" +
            "Please use this code on the Forgot Password page to reset your password.\n" +
            "This code is valid for a single use only.\n\n" +
            "If you did not request a password reset, please ignore this email.\n\n" +
            "Best regards,\nQP Management System Team",
            username, code
        );
    }
}