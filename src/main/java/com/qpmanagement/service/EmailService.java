package com.qpmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    @Value("${mailtrap.username}")
    private String mailtrapUsername;

    @Value("${mailtrap.password}")
    private String mailtrapPassword;

    public void sendPasswordResetCode(String toEmail, String username, String code) {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("sandbox.smtp.mailtrap.io");
            mailSender.setPort(2525);
            mailSender.setUsername(mailtrapUsername);
            mailSender.setPassword(mailtrapPassword);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@qpmanagement.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset Code - QP Management System");
            message.setText(buildPasswordResetEmail(username, code));

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
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