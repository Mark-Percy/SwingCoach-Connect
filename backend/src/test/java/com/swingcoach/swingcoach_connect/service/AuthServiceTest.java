package com.swingcoach.swingcoach_connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;

import com.swingcoach.swingcoach_connect.dto.auth.AuthResponse;
import com.swingcoach.swingcoach_connect.dto.auth.RegisterRequest;
import com.swingcoach.swingcoach_connect.dto.auth.SignInRequest;
import com.swingcoach.swingcoach_connect.repository.UserRepository;
import com.swingcoach.swingcoach_connect.service.security.JwtService;
import com.swingcoach.swingcoach_connect.model.User;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    protected AuthenticationManager authenticationManager;
    
    @Mock
    protected JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
   
    @Nested
    class RegisterUserTests {
        
        @BeforeEach
        void setup() {
            registerRequest = new RegisterRequest(
                "test@example.com",
                "Password1",
                "John",
                "Doe",
                "1213454545554",
                LocalDate.of(1990, 1, 1)
            );
        }
            
        // Testing for registering a user
        @Test
        void testRegisterUser_success() {
            when(userRepository.findByEmail(registerRequest.getEmail()))
            .thenReturn(Optional.empty());
            
            when(passwordEncoder.encode(anyString()))
            .thenReturn("hashedPasswordFromEncoder");
            
            when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            
            AuthResponse response = authService.registerUser(registerRequest);
    
            assertNotNull(response, "AuthResponse should not be null");
            assertEquals("Registration Successful! Please verify your email.", response.getMessage());
            assertEquals(null, response.getToken(), "The token should be as expected");
            
            // Verify interaction with mocks
            verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
            verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        void testRegisterUser_emailAlreadyExists() {
            when(userRepository.findByEmail(registerRequest.getEmail()))
            .thenReturn(Optional.of(new User()));
            
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.registerUser(registerRequest);
            });
            
            assertEquals("Email already registered: " + registerRequest.getEmail(), exception.getMessage());
            
            verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
            verify(passwordEncoder, never()).encode(registerRequest.getPassword());
            verify(userRepository, never()).save(any(User.class));
        }
    }
    @Nested
    class SignIn {

        SignInRequest signInRequest;
        User existingUser;
        Authentication successfulAuthentication;

        @BeforeEach
        void setup() {
           signInRequest = new SignInRequest("signin@example.com", "CorrectPassword123!");

            // Initialize existingUser as a real User object that implements UserDetails
            existingUser = new User();
            existingUser.setEmail(signInRequest.getEmail());
            existingUser.setPasswordHash("hashedPassword");
            existingUser.setFirstName("Existing");
            existingUser.setLastName("User");
            existingUser.setAccountStatus(User.AccountStatus.ACTIVE);
            existingUser.setIsEmailVerified(true);

            // Mock the successful Authentication object
            successfulAuthentication = mock(Authentication.class);
            lenient().when(successfulAuthentication.isAuthenticated()).thenReturn(true);
            // When getPrincipal() is called on the successfulAuthentication mock, return existingUser
            lenient().when(successfulAuthentication.getPrincipal()).thenReturn(existingUser);
            SecurityContextHolder.clearContext();

        }

        @Test
        void testSignIn_success() {
            when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
            )).thenReturn(successfulAuthentication);

            when(jwtService.generateToken(any(UserDetails.class))).thenReturn("dummy.jwt.token");

            AuthResponse response = authService.signIn(signInRequest);

            assertNotNull(response, "AuthResponse should not be null");
            assertEquals("Sign in successful for user: " + signInRequest.getEmail(), response.getMessage(), "The success message should be as expected");
            assertNotNull(response.getToken(), "Token should not be null");
            assertEquals("dummy.jwt.token", response.getToken(), "The token should be the dummy JWT");

            verify(authenticationManager, times(1)).authenticate(
                argThat(token ->
                    token instanceof UsernamePasswordAuthenticationToken &&
                    ((UsernamePasswordAuthenticationToken) token).getPrincipal().equals(signInRequest.getEmail()) &&
                    ((UsernamePasswordAuthenticationToken) token).getCredentials().equals(signInRequest.getPassword())
                )
            );
            verify(successfulAuthentication, times(1)).getPrincipal();
            verify(jwtService, times(1)).generateToken(any(UserDetails.class));
        }

        @Test
        void testSignIn_failIncorrectEmail() {
            signInRequest = new SignInRequest("test@testing.com", "password1");
            when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
            )).thenThrow(new AuthenticationException("Bad credentials") {});

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.signIn(signInRequest);
            });

            assertEquals("Invalid email or password", exception.getMessage());

            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }
}
        