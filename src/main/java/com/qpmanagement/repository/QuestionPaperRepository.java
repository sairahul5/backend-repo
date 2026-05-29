package com.qpmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qpmanagement.entity.QuestionPaper;
import com.qpmanagement.entity.User;

@Repository
public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, Long> {
    List<QuestionPaper> findByStatus(QuestionPaper.Status status);
    List<QuestionPaper> findByUploadedBy(User user);
    List<QuestionPaper> findByExamNameContaining(String examName);
    List<QuestionPaper> findByBatchYear(String batchYear);
    long countByStatus(QuestionPaper.Status status);
}
