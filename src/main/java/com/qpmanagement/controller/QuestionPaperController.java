package com.qpmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qpmanagement.dto.ApiResponse;
import com.qpmanagement.entity.QuestionPaper;
import com.qpmanagement.service.QuestionPaperService;

@RestController
@RequestMapping("/api/question-papers")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionPaperController {
    
    @Autowired
    private QuestionPaperService questionPaperService;
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<QuestionPaper>> uploadQuestionPaper(
            @RequestParam("file") MultipartFile file,
            @RequestParam("examName") String examName,
            @RequestParam("paperNumber") String paperNumber,
            @RequestParam("batchYear") String batchYear,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            QuestionPaper questionPaper = questionPaperService.uploadQuestionPaper(
                    file, examName, paperNumber, batchYear, username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Question paper uploaded successfully", questionPaper));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionPaper>>> getAllQuestionPapers() {
        List<QuestionPaper> questionPapers = questionPaperService.getApprovedQuestionPapers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Question papers retrieved successfully", questionPapers));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<QuestionPaper>>> getPendingQuestionPapers() {
        List<QuestionPaper> questionPapers = questionPaperService.getPendingQuestionPapers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending question papers retrieved", questionPapers));
    }
    
    @PutMapping("/verify/{id}")
    public ResponseEntity<ApiResponse<QuestionPaper>> verifyQuestionPaper(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            QuestionPaper questionPaper = questionPaperService.verifyQuestionPaper(id, status, username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Question paper verified", questionPaper));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<QuestionPaper>>> filterQuestionPapers(
            @RequestParam(required = false) String examName,
            @RequestParam(required = false) String batchYear) {
        List<QuestionPaper> questionPapers = questionPaperService.filterQuestionPapers(examName, batchYear);
        return ResponseEntity.ok(new ApiResponse<>(true, "Filtered results", questionPapers));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionPaper>> updateQuestionPaper(
            @PathVariable Long id,
            @RequestParam String examName,
            @RequestParam String paperNumber,
            @RequestParam String batchYear) {
        try {
            QuestionPaper questionPaper = questionPaperService.updateQuestionPaper(id, examName, paperNumber, batchYear);
            return ResponseEntity.ok(new ApiResponse<>(true, "Question paper updated", questionPaper));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionPaper(@PathVariable Long id) {
        try {
            questionPaperService.deleteQuestionPaper(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Question paper deleted", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
