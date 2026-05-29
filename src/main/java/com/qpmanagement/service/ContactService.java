package com.qpmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.qpmanagement.entity.ContactMessage;
import com.qpmanagement.repository.ContactMessageRepository;

@Service
public class ContactService {
    
    @Autowired
    private ContactMessageRepository contactMessageRepository;
    
    public ContactMessage createMessage(String name, String email, String message) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setMessage(message);
        contactMessage.setStatus(ContactMessage.Status.UNREAD);
        
        return contactMessageRepository.save(contactMessage);
    }
    
    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }
    
    public List<ContactMessage> getUnreadMessages() {
        return contactMessageRepository.findByStatus(ContactMessage.Status.UNREAD);
    }
    
    public ContactMessage markAsRead(@NonNull Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setStatus(ContactMessage.Status.READ);
        return contactMessageRepository.save(message);
    }
}
