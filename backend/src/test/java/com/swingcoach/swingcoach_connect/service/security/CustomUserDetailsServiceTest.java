package com.swingcoach.swingcoach_connect.service.security;

import com.swingcoach.swingcoach_connect.model.Role;
import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	private User dummyUser;

	@BeforeEach
	void setUp() {
		dummyUser = new User();
		dummyUser.setEmail("test@example.com");
		dummyUser.setPasswordHash("hashedPassword123");
		dummyUser.setFirstName("Test");
		dummyUser.setLastName("User");
		dummyUser.setAccountStatus(User.AccountStatus.ACTIVE);
		dummyUser.setIsEmailVerified(true);
	}

	@Test
	void testLoadUserByUsername_Success() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(dummyUser.getEmail());

		// THEN: Assert UserDetails properties
		assertNotNull(userDetails);
		assertEquals(dummyUser.getEmail(), userDetails.getUsername());
		assertEquals(dummyUser.getPasswordHash(), userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().isEmpty()); // No roles yet

		verify(userRepository, times(1)).findByEmail(dummyUser.getEmail());
	}

	@Test
	void testLoadUserByUsername_UserNotFound() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
			customUserDetailsService.loadUserByUsername("nonexistent@example.com");
		});

		assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
		verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
	}

	@Test
	void testLoadUserByUsername_WithRoles() {

		User userWithRoles = new User();
		userWithRoles.setEmail("user.with.roles@example.com");
		userWithRoles.setPasswordHash("hashedPasswordWithRoles");
		userWithRoles.setFirstName("Role");
		userWithRoles.setLastName("User");
		userWithRoles.setAccountStatus(User.AccountStatus.ACTIVE);
		userWithRoles.setIsEmailVerified(true);

		Role studentRole = new Role();
		studentRole.setName("STUDENT");
		studentRole.setId(1);
		Role adminRole = new Role();
		adminRole.setName("ADMIN");
		adminRole.setId(2);
	
		Set<Role> roles = new HashSet<>();
		roles.add(studentRole);
		roles.add(adminRole);
		userWithRoles.setRoles(roles);
		logger.info("Info: "+ userWithRoles.getRoles());
	
		when(userRepository.findByEmail(userWithRoles.getEmail())).thenReturn(Optional.of(userWithRoles));
	
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userWithRoles.getEmail());
		logger.info("Info: "+ userDetails.getAuthorities());
	
		assertNotNull(userDetails);
		assertEquals(userWithRoles.getEmail(), userDetails.getUsername());
		assertEquals(userWithRoles.getPasswordHash(), userDetails.getPassword());
	
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		assertNotNull(authorities);
		assertEquals(2, authorities.size(), "User should have 2 authorities");
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT")), "Should contain ROLE_STUDENT");
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")), "Should contain ROLE_ADMIN");
	
		verify(userRepository, times(1)).findByEmail(userWithRoles.getEmail());
	}
	
}