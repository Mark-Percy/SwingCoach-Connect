package com.swingcoach.swingcoach_connect.repository;

import com.swingcoach.swingcoach_connect.model.Role;
import com.swingcoach.swingcoach_connect.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	User user;

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);

	@BeforeEach
	void createUser() {
		user = new User();
		user.setEmail("test@example.com");
		user.setPasswordHash("hashedpassword123");
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPhoneNumber("1234567890");
		user.setDateOfBirth(LocalDate.of(1990, 5, 15));
		user.setAccountStatus(User.AccountStatus.PENDING_EMAIL_VERIFICATION);
		user.setIsEmailVerified(false);
		user.setVerificationToken("some_token_123");
		user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

	}

	@Test
	void testSaveUser() {
		User savedUser = userRepository.save(user);

		assertThat(savedUser).isNotNull();

		logger.info("User created with ID: " + savedUser.getId());
		assertThat(savedUser.getId()).isNotNull();
		assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
		assertThat(savedUser.getAccountStatus()).isEqualTo(User.AccountStatus.PENDING_EMAIL_VERIFICATION);
		assertThat(savedUser.getIsEmailVerified()).isFalse();

		// Verify that the user can be found by email
		Optional<User> foundUser = userRepository.findByEmail("test@example.com");
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getFirstName()).isEqualTo("John");
	}

	@Test
	void testFindByEmailNotFound() {
		// When: Searching for an email that does not exist
		Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

		// Then: The Optional should be empty
		assertThat(foundUser).isNotPresent();
	}

	@Test
	void testSaveUserWithRolesAndLoadRoles() {
		Role role = new Role();
		role.setName("Student");
		Role savedRole = roleRepository.save(role);
		logger.info("Role created with ID: " + savedRole.getId());

		assertNotNull(savedRole.getId(), "Saved role shouldn't be null");
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(savedRole);
		user.setRoles(userRoles);

		User savedUser = userRepository.save(user);
		assertNotNull(savedUser.getId(), "Saved user shouldn't be null");
		logger.info("User created with ID: " + savedUser.getId());

		Optional<User> foundUserOptional = userRepository.findById(savedUser.getId());
		assertTrue(foundUserOptional.isPresent(), "User should be found");

		User foundUser = foundUserOptional.get();

		assertNotNull(foundUser.getRoles(), "User should have roles");
		assertEquals(1, foundUser.getRoles().size(), "There should be one role");

		Role retrievedRole = foundUser.getRoles().iterator().next();
		assertEquals(savedRole.getName(), retrievedRole.getName(), "Retrieved name should be as was saved");
		assertEquals(savedRole.getId(), retrievedRole.getId(), "Retrieved Id should be as was saved.");
	}
}
