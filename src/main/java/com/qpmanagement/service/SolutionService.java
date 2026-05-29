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

import com.qpmanagement.entity.Solution;
import com.qpmanagement.entity.User;
import com.qpmanagement.repository.SolutionRepository;
import com.qpmanagement.repository.UserRepository;

@Service
public class SolutionService {
    
    @Autowired
    private SolutionRepository solutionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public Solution createSolution(String title, String description, String question, String videoUrl,
                                   String platform, MultipartFile thumbnail, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Solution solution = new Solution();
        solution.setTitle(title);
        solution.setDescription(description);
        solution.setQuestion(question);
        solution.setVideoUrl(videoUrl);
        solution.setPlatform(Solution.Platform.valueOf(platform.toUpperCase()));
        solution.setCreatedBy(user);
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir + "/solutions");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String filename = UUID.randomUUID().toString() + "-" + thumbnail.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(thumbnail.getInputStream(), filePath);
            
            solution.setThumbnailPath("/uploads/solutions/" + filename);
        }
        
        return solutionRepository.save(solution);
    }
    
    public List<Solution> getAllSolutions() {
        return solutionRepository.findAll();
    }
    
    public List<Solution> getSolutionsByPlatform(String platform) {
        return solutionRepository.findByPlatform(Solution.Platform.valueOf(platform.toUpperCase()));
    }
    
    public void deleteSolution(@NonNull Long id) {
        Solution solution = solutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solution not found"));
        
        // Delete physical file if exists
        if (solution.getThumbnailPath() != null) {
            try {
                Path filePath = Paths.get(uploadDir + solution.getThumbnailPath().replace("/uploads", ""));
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but continue with database deletion
            }
        }
        
        solutionRepository.deleteById(id);
    }
    
    public Solution updateSolution(@NonNull Long id, String title, String description, String question, 
                                   String videoUrl, String platform, MultipartFile thumbnail) throws IOException {
        Solution solution = solutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solution not found"));
        
        if (title != null) solution.setTitle(title);
        if (description != null) solution.setDescription(description);
        if (question != null) solution.setQuestion(question);
        if (videoUrl != null) solution.setVideoUrl(videoUrl);
        if (platform != null) solution.setPlatform(Solution.Platform.valueOf(platform.toUpperCase()));
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // Delete old thumbnail if exists
            if (solution.getThumbnailPath() != null) {
                try {
                    Path oldFilePath = Paths.get(uploadDir + solution.getThumbnailPath().replace("/uploads", ""));
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    // Continue
                }
            }
            
            // Save new thumbnail
            Path uploadPath = Paths.get(uploadDir + "/solutions");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String filename = UUID.randomUUID().toString() + "-" + thumbnail.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(thumbnail.getInputStream(), filePath);
            
            solution.setThumbnailPath("/uploads/solutions/" + filename);
        }
        
        return solutionRepository.save(solution);
    }
}
