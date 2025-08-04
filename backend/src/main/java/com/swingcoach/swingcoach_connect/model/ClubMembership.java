package com.swingcoach.swingcoach_connect.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clubMemberships")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_in_club", nullable = false)
    private ClubRoleInClub roleInClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_level_id")
    private Role studentLevel;

    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;

    @Column(columnDefinition = "TEXT")
    private String bannedReason;

    private LocalDateTime bannedUntilDate;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ClubRoleInClub {
        CLUB_ADMIN,
        CLUB_STAFF,
        TEACHER,
        STUDENT
    }
}
