package com.swingcoach.swingcoach_connect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "user_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	@ToString.Exclude
	private Set<Role> roles = new HashSet<>();


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	private String phoneNumber;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_status", nullable = false)
	private AccountStatus accountStatus;

	@Column(name = "is_email_verified", nullable = false)
	private Boolean isEmailVerified = false;

	@Column(name = "verification_token")
	private String verificationToken;

	@Column(name = "verification_token_expiry")
	private LocalDateTime verificationTokenExpiry;

	@Column(name = "password_reset_token")
	private String passwordResetToken;

	@Column(name = "password_reset_token_expiry")
	private LocalDateTime passwordResetTokenExpiry;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// Enum for account status
	public enum AccountStatus {
		ACTIVE, LOCKED, DEACTIVATED, PENDING_EMAIL_VERIFICATION
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
			.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.passwordHash;
	}

	@Override
	public String getUsername() {
		return this.email;
	}
}
