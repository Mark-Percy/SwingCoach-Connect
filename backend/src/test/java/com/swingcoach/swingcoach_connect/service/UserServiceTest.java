package com.swingcoach.swingcoach_connect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.model.User.AccountStatus;
import com.swingcoach.swingcoach_connect.repository.UserRepository;

import org.mockito.InjectMocks;
import org.mockito.Mock;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	UserService userService;
	
	User dummyUser;
	@BeforeEach
	void setUp() {
		dummyUser = new User();
		dummyUser.setId(1L);
		dummyUser.setEmail("test@example.com");
		dummyUser.setPasswordHash("hashedPassword");
		dummyUser.setFirstName("Test");
		dummyUser.setLastName("User");
		dummyUser.setIsEmailVerified(true);
		dummyUser.setPhoneNumber("1234567890");
		dummyUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
	}
	
	@Test
	void test_getExistingUser() {
		dummyUser.setAccountStatus(AccountStatus.ACTIVE);

		when(userRepository.findByEmailStatus(dummyUser.getEmail(), AccountStatus.ACTIVE)).thenReturn(Optional.of(dummyUser));
		Optional<User> userOptional = userService.findByEmail(dummyUser.getEmail());
		assertTrue(userOptional.isPresent());
		assertEquals(dummyUser.getEmail(), userOptional.get().getEmail());
		verify(userRepository, times(1)).findByEmailStatus(dummyUser.getEmail(), AccountStatus.ACTIVE);
	}

	@Test
	void test_getExistingInactiveUser() {
		dummyUser.setAccountStatus(AccountStatus.PENDING_EMAIL_VERIFICATION);

		when(userRepository.findByEmailStatus(dummyUser.getEmail(), AccountStatus.ACTIVE)).thenReturn(Optional.empty());

		Optional<User> userOptional = userService.findByEmail(dummyUser.getEmail());
		assertFalse(userOptional.isPresent());
		verify(userRepository, times(1)).findByEmailStatus(dummyUser.getEmail(), AccountStatus.ACTIVE);
	}

	@Test
	void test_getUserNotFound() {
		dummyUser.setAccountStatus(AccountStatus.PENDING_EMAIL_VERIFICATION);

		when(userRepository.findByEmailStatus("Test@Testing.com", AccountStatus.ACTIVE)).thenReturn(Optional.empty());

		Optional<User> userOptional = userService.findByEmail("Test@Testing.com");
		assertFalse(userOptional.isPresent());
		verify(userRepository, times(1)).findByEmailStatus("Test@Testing.com", AccountStatus.ACTIVE);
	}
}
