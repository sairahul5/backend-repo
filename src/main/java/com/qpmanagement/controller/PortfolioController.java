package com.qpmanagement.controller;

import java.io.IOException;
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
import com.qpmanagement.entity.Project;
import com.qpmanagement.service.ProjectService;

@RestController
@RequestMapping("/api/portfolio")

public class PortfolioController {
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Project>>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(new ApiResponse<>(true, "Projects retrieved successfully", projects));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByStatus(@RequestParam String status) {
        List<Project> projects = projectService.getProjectsByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Filtered projects", projects));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Project>> createProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam(value = "projectUrl", required = false) String projectUrl,
            @RequestParam(value = "technologies", required = false) String technologies,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Project project = projectService.createProject(title, description, status, 
                    projectUrl, technologies, image, username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Project created successfully", project));
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Project>> updateProject(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String projectUrl,
            @RequestParam(required = false) String technologies,
            @RequestParam(required = false) MultipartFile image) {
        try {
            Project project = projectService.updateProject(id, title, description, status, 
                    projectUrl, technologies, image);
            return ResponseEntity.ok(new ApiResponse<>(true, "Project updated successfully", project));
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Project deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
