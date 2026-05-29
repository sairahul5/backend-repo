package com.qpmanagement.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

@Service
public class MfaService {
    
    private final GoogleAuthenticator googleAuthenticator;
    
    public MfaService() {
        // Configure with more lenient time window (allows 1 window before and after current)
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(30000) // 30 seconds
                .setWindowSize(2) // Allow 2 windows before/after (60 seconds total tolerance)
                .setCodeDigits(6)
                .build();
        
        this.googleAuthenticator = new GoogleAuthenticator(config);
    }
    
    /**
     * Generate a new MFA secret for a user
     */
    public String generateSecret() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }
    
    /**
     * Generate QR code URL for Google Authenticator
     */
    public String generateQRUrl(String email, String secret, String issuer) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(
            issuer,
            email,
            new GoogleAuthenticatorKey.Builder(secret).build()
        );
    }
    
    /**
     * Generate QR code image as base64 string
     */
    public String generateQRCodeImage(String qrUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrUrl, BarcodeFormat.QR_CODE, 250, 250);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    /**
     * Verify TOTP code
     */
    public boolean verifyCode(String secret, int code) {
        return googleAuthenticator.authorize(secret, code);
    }
    
    /**
     * Verify TOTP code from string
     */
    public boolean verifyCode(String secret, String code) {
        try {
            int codeInt = Integer.parseInt(code);
            return verifyCode(secret, codeInt);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
