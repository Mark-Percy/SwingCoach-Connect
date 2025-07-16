package com.swingcoach.swingcoach_connect.service.security;

import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

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
}