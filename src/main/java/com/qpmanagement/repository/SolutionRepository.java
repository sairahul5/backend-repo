package com.qpmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qpmanagement.entity.Solution;
import com.qpmanagement.entity.User;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findByPlatform(Solution.Platform platform);
    List<Solution> findByCreatedBy(User user);
}
