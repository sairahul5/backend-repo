package com.qpmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qpmanagement.entity.Project;
import com.qpmanagement.entity.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(Project.Status status);
    List<Project> findByCreatedBy(User user);
    long countByStatus(Project.Status status);
}
