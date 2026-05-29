package com.qpmanagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qpmanagement.entity.QuestionPaper;
import com.qpmanagement.entity.User;
import com.qpmanagement.repository.QuestionPaperRepository;
import com.qpmanagement.repository.UserRepository;

@Service
public class QuestionPaperService {
    
    @Autowired
    private QuestionPaperRepository questionPaperRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public QuestionPaper uploadQuestionPaper(MultipartFile file, String examName, String paperNumber, 
                                             String batchYear, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir + "/question-papers");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        
        // Save file
        Files.copy(file.getInputStream(), filePath);
        
        QuestionPaper questionPaper = new QuestionPaper();
        questionPaper.setExamName(examName);
        questionPaper.setPaperNumber(paperNumber);
        questionPaper.setBatchYear(batchYear);
        questionPaper.setFilePath("/uploads/question-papers/" + filename);
        questionPaper.setUploadedBy(user);
        
        // Admin and Editor uploads are auto-approved
        if (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.EDITOR) {
            questionPaper.setStatus(QuestionPaper.Status.APPROVED);
            questionPaper.setVerifiedBy(user);
            questionPaper.setVerifiedAt(LocalDateTime.now());
        } else {
            questionPaper.setStatus(QuestionPaper.Status.PENDING);
        }
        
        return questionPaperRepository.save(questionPaper);
    }
    
    public List<QuestionPaper> getAllQuestionPapers() {
        return questionPaperRepository.findAll();
    }
    
    public List<QuestionPaper> getApprovedQuestionPapers() {
        return questionPaperRepository.findByStatus(QuestionPaper.Status.APPROVED);
    }
    
    public List<QuestionPaper> getPendingQuestionPapers() {
        return questionPaperRepository.findByStatus(QuestionPaper.Status.PENDING);
    }
    
    public QuestionPaper verifyQuestionPaper(@NonNull Long id, String status, String username) {
        QuestionPaper questionPaper = questionPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question paper not found"));
        
        User verifier = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        questionPaper.setStatus(QuestionPaper.Status.valueOf(status.toUpperCase()));
        questionPaper.setVerifiedBy(verifier);
        questionPaper.setVerifiedAt(LocalDateTime.now());
        
        return questionPaperRepository.save(questionPaper);
    }
    
    public List<QuestionPaper> filterQuestionPapers(String examName, String batchYear) {
        if (examName != null && !examName.isEmpty()) {
            return questionPaperRepository.findByExamNameContaining(examName);
        } else if (batchYear != null && !batchYear.isEmpty()) {
            return questionPaperRepository.findByBatchYear(batchYear);
        }
        return questionPaperRepository.findByStatus(QuestionPaper.Status.APPROVED);
    }
    
    public QuestionPaper updateQuestionPaper(@NonNull Long id, String examName, String paperNumber, String batchYear) {
        QuestionPaper questionPaper = questionPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question paper not found"));
        
        questionPaper.setExamName(examName);
        questionPaper.setPaperNumber(paperNumber);
        questionPaper.setBatchYear(batchYear);
        
        return questionPaperRepository.save(questionPaper);
    }
    
    public void deleteQuestionPaper(@NonNull Long id) {
        QuestionPaper questionPaper = questionPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question paper not found"));
        
        // Delete physical file
        try {
            Path filePath = Paths.get(uploadDir + questionPaper.getFilePath().replace("/uploads", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but continue with database deletion
        }
        
        questionPaperRepository.deleteById(id);
    }
}
