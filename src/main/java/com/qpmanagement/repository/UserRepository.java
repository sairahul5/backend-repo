package com.qpmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.qpmanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(@NonNull String email);
    boolean existsByEmail(@NonNull String email);
    List<User> findByRole(User.Role role);
    long countByRole(User.Role role);
    
    // Username is now the primary key, so findById works with username
    // But we can add this for clarity
    default Optional<User> findByUsername(@NonNull String username) {
        return findById(username);
    }
    
    default boolean existsByUsername(@NonNull String username) {
        return existsById(username);
    }
}
