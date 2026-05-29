package com.qpmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@qpmanagement.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetCode(String toEmail, String username, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Code - QP Management System");
            message.setText(buildPasswordResetEmail(username, code));
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log the error but don't throw exception
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            // In production, you might want to use proper logging
        }
    }

    private String buildPasswordResetEmail(String username, String code) {
        return String.format(
            "Dear %s,\n\n" +
            "Your password reset request has been approved.\n\n" +
            "Your password reset code is: %s\n\n" +
            "Please use this code on the 'Forgot Password' page to reset your password.\n" +
            "This code is valid for a single use only.\n\n" +
            "If you did not request a password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "QP Management System Team",
            username, code
        );
    }
}
