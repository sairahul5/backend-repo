package com.qpmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qpmanagement.service.PasswordResetService;

@Component
public class PasswordResetCleanupTask {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Run every hour to clean up completed password reset requests older than 24 hours
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void cleanupOldPasswordResetRequests() {
        passwordResetService.cleanupOldRequests();
    }
}
