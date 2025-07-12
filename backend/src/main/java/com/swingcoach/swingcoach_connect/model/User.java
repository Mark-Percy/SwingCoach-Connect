package com.swingcoach.swingcoach_connect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*; // Use jakarta.persistence for Spring Boot 3+

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Matches your SQL table name
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates no-argument constructor
@AllArgsConstructor // Lombok: Generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // Stores hashed password

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; // Use LocalDate for DATE type

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false; // Default to false

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry; // Use LocalDateTime for DATETIME

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp // Automatically sets creation timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically updates on entity modification
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enum for account status
    public enum AccountStatus {
        ACTIVE, LOCKED, DEACTIVATED, PENDING_EMAIL_VERIFICATION
    }
}
