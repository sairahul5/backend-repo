package com.qpmanagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qpmanagement.entity.Project;
import com.qpmanagement.entity.User;
import com.qpmanagement.repository.ProjectRepository;
import com.qpmanagement.repository.UserRepository;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public Project createProject(String title, String description, String status, String projectUrl,
                                 String technologies, MultipartFile image, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setStatus(Project.Status.valueOf(status.toUpperCase()));
        project.setProjectUrl(projectUrl);
        project.setTechnologies(technologies);
        project.setCreatedBy(user);
        
        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir + "/projects");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String filename = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(image.getInputStream(), filePath);
            
            project.setImagePath("/uploads/projects/" + filename);
        }
        
        return projectRepository.save(project);
    }
    
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(Project.Status.valueOf(status.toUpperCase()));
    }
    
    public Project updateProject(@NonNull Long id, String title, String description, String status,
                                String projectUrl, String technologies, MultipartFile image) throws IOException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (title != null) project.setTitle(title);
        if (description != null) project.setDescription(description);
        if (status != null) project.setStatus(Project.Status.valueOf(status.toUpperCase()));
        if (projectUrl != null) project.setProjectUrl(projectUrl);
        if (technologies != null) project.setTechnologies(technologies);
        
        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir + "/projects");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String filename = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(image.getInputStream(), filePath);
            
            project.setImagePath("/uploads/projects/" + filename);
        }
        
        return projectRepository.save(project);
    }
    
    public void deleteProject(@NonNull Long id) {
        projectRepository.deleteById(id);
    }
}
