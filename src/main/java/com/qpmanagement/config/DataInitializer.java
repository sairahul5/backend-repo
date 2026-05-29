package com.qpmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.qpmanagement.entity.User;
import com.qpmanagement.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin1")) {
            User admin = new User();
            admin.setUsername("admin1");
            admin.setName("Admin");
            admin.setEmail("admin1@sairahul.adabala");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(User.Role.ADMIN);
            
            userRepository.save(admin);
            System.out.println("✅ Default admin user created:");
            System.out.println("   Username: admin1");
            System.out.println("   Email: admin1@sairahul.adabala");
            System.out.println("   Password: Admin@123");
        }
        
        // Create default editor user if not exists
        if (!userRepository.existsByUsername("editor1")) {
            User editor = new User();
            editor.setUsername("editor1");
            editor.setName("Editor");
            editor.setEmail("editor1@sairahul.adabala");
            editor.setPassword(passwordEncoder.encode("Editor@123"));
            editor.setRole(User.Role.EDITOR);
            
            userRepository.save(editor);
            System.out.println("✅ Default editor user created:");
            System.out.println("   Username: editor1");
            System.out.println("   Email: editor1@sairahul.adabala");
            System.out.println("   Password: Editor@123");
        }
    }
}
