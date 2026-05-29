package com.qpmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.qpmanagement.entity.Solution;
import com.qpmanagement.service.SolutionService;

@RestController
@RequestMapping("/api/solutions")
@CrossOrigin(origins = "http://localhost:3000")
public class SolutionController {
    
    @Autowired
    private SolutionService solutionService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Solution>>> getAllSolutions() {
        List<Solution> solutions = solutionService.getAllSolutions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Solutions retrieved successfully", solutions));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Solution>>> getSolutionsByPlatform(@RequestParam String platform) {
        List<Solution> solutions = solutionService.getSolutionsByPlatform(platform);
        return ResponseEntity.ok(new ApiResponse<>(true, "Filtered solutions", solutions));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Solution>> createSolution(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("question") String question,
            @RequestParam("videoUrl") String videoUrl,
            @RequestParam("platform") String platform,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Solution solution = solutionService.createSolution(title, description, question, 
                    videoUrl, platform, thumbnail, username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Solution created successfully", solution));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSolution(@PathVariable Long id) {
        try {
            solutionService.deleteSolution(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Solution deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Solution>> updateSolution(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String question,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) MultipartFile thumbnail) {
        try {
            Solution solution = solutionService.updateSolution(id, title, description, question, 
                    videoUrl, platform, thumbnail);
            return ResponseEntity.ok(new ApiResponse<>(true, "Solution updated successfully", solution));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
