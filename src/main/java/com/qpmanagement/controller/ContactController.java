package com.qpmanagement.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpmanagement.dto.ApiResponse;
import com.qpmanagement.entity.ContactMessage;
import com.qpmanagement.service.ContactService;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {
    
    @Autowired
    private ContactService contactService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ContactMessage>> createMessage(@RequestBody Map<String, String> request) {
        try {
            ContactMessage message = contactService.createMessage(
                    request.get("name"),
                    request.get("email"),
                    request.get("message")
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Message sent successfully", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
