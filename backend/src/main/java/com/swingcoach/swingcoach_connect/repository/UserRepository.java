package com.swingcoach.swingcoach_connect.repository;

import com.swingcoach.swingcoach_connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom method to find a user by email
    Optional<User> findByEmail(String email);
}
