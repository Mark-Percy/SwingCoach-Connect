package com.swingcoach.swingcoach_connect.repository;

import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.model.User.AccountStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndAccountStatus(String email, AccountStatus accountStatus);
}
