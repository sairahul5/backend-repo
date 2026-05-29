package com.qpmanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpmanagement.dto.ApiResponse;
import com.qpmanagement.dto.PasswordResetAcceptDTO;
import com.qpmanagement.dto.PasswordResetResponse;
import com.qpmanagement.entity.ContactMessage;
import com.qpmanagement.entity.User;
import com.qpmanagement.repository.ContactMessageRepository;
import com.qpmanagement.repository.ProjectRepository;
import com.qpmanagement.repository.QuestionPaperRepository;
import com.qpmanagement.repository.SolutionRepository;
import com.qpmanagement.repository.UserRepository;
import com.qpmanagement.service.ContactService;
import com.qpmanagement.service.PasswordResetService;

@RestController
@RequestMapping("/api/admin")

@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private QuestionPaperRepository questionPaperRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private SolutionRepository solutionRepository;
    
    @Autowired
    private ContactMessageRepository contactMessageRepository;
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<ContactMessage>>> getAllMessages() {
        List<ContactMessage> messages = contactService.getAllMessages();
        return ResponseEntity.ok(new ApiResponse<>(true, "Messages retrieved", messages));
    }
    
    @GetMapping("/question-papers")
    public ResponseEntity<ApiResponse<List<com.qpmanagement.entity.QuestionPaper>>> getAllQuestionPapersForAdmin() {
        List<com.qpmanagement.entity.QuestionPaper> papers = questionPaperRepository.findAll();
        System.out.println("DEBUG: findAll() returned " + papers.size() + " papers");
        for (com.qpmanagement.entity.QuestionPaper paper : papers) {
            System.out.println("DEBUG: Paper ID=" + paper.getId() + ", ExamName=" + paper.getExamName() + ", Status=" + paper.getStatus());
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "All question papers retrieved", papers));
    }
    
    @GetMapping("/messages/unread")
    public ResponseEntity<ApiResponse<List<ContactMessage>>> getUnreadMessages() {
        List<ContactMessage> messages = contactService.getUnreadMessages();
        return ResponseEntity.ok(new ApiResponse<>(true, "Unread messages retrieved", messages));
    }
    
    @PutMapping("/messages/{id}/read")
    public ResponseEntity<ApiResponse<ContactMessage>> markMessageAsRead(@PathVariable Long id) {
        try {
            ContactMessage message = contactService.markAsRead(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Message marked as read", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/create-editor")
    public ResponseEntity<ApiResponse<User>> createEditor(@RequestBody Map<String, String> request) {
        try {
            if (userRepository.existsByEmail(request.get("email"))) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Email already exists", null));
            }
            
            User editor = new User();
            editor.setName(request.get("name"));
            editor.setEmail(request.get("email"));
            editor.setPassword(passwordEncoder.encode(request.get("password")));
            editor.setRole(User.Role.EDITOR);
            
            String username = request.get("username");
            editor.setUsername(username);
            userRepository.save(editor);
            return ResponseEntity.ok(new ApiResponse<>(true, "Editor created successfully", editor));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    // Dashboard Statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalPapers", questionPaperRepository.count());
        stats.put("pendingPapers", questionPaperRepository.countByStatus(com.qpmanagement.entity.QuestionPaper.Status.PENDING));
        stats.put("approvedPapers", questionPaperRepository.countByStatus(com.qpmanagement.entity.QuestionPaper.Status.APPROVED));
        stats.put("totalProjects", projectRepository.count());
        stats.put("completedProjects", projectRepository.countByStatus(com.qpmanagement.entity.Project.Status.COMPLETED));
        stats.put("inProgressProjects", projectRepository.countByStatus(com.qpmanagement.entity.Project.Status.IN_PROGRESS));
        stats.put("totalSolutions", solutionRepository.count());
        stats.put("totalMessages", contactMessageRepository.count());
        stats.put("unreadMessages", contactMessageRepository.countByStatus(ContactMessage.Status.UNREAD));
        stats.put("totalEditors", userRepository.countByRole(User.Role.EDITOR));
        stats.put("totalUsers", userRepository.countByRole(User.Role.USER));
        return ResponseEntity.ok(new ApiResponse<>(true, "Stats retrieved", stats));
    }
    
    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved", users));
    }
    
    @DeleteMapping("/users/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        try {
            userRepository.deleteById(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/users/{username}/role")
    public ResponseEntity<ApiResponse<User>> updateUserRole(@PathVariable @NonNull String username, @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setRole(User.Role.valueOf(request.get("role")));
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse<>(true, "User role updated", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    // Message Management
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Long id) {
        try {
            contactMessageRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Message deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/editors/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteEditor(@PathVariable String username) {
        try {
            userRepository.deleteById(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Editor deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/editors")
    public ResponseEntity<ApiResponse<List<User>>> getAllEditors() {
        List<User> editors = userRepository.findByRole(User.Role.EDITOR);
        return ResponseEntity.ok(new ApiResponse<>(true, "Editors retrieved", editors));
    }
    
    // Password Reset Management
    @GetMapping("/password-reset/all")
    public ResponseEntity<ApiResponse<List<PasswordResetResponse>>> getAllPasswordResetRequests() {
        try {
            List<PasswordResetResponse> requests = passwordResetService.getAllResetRequests();
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset requests retrieved", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/password-reset/accept")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> acceptPasswordResetRequest(@RequestBody PasswordResetAcceptDTO acceptDTO) {
        try {
            PasswordResetResponse response = passwordResetService.acceptResetRequest(acceptDTO.getRequestId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset request accepted and code sent to user", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
