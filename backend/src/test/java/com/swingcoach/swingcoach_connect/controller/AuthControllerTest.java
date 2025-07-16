package com.swingcoach.swingcoach_connect.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.swingcoach.swingcoach_connect.dto.auth.AuthResponse;
import com.swingcoach.swingcoach_connect.dto.auth.RegisterRequest;
import com.swingcoach.swingcoach_connect.dto.auth.SignInRequest;
import com.swingcoach.swingcoach_connect.service.AuthService;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;
    
    
    @Nested
    class RegisterRequest_tests {
        
        private RegisterRequest validRegisterRequest;
        private RegisterRequest invalidRegisterRequest;

        @BeforeEach
        void setup() {
            validRegisterRequest = new RegisterRequest(
                "test@examle.com",
                "Password123",
                "John",
                "Doe",
                "11122321321",
                LocalDate.of(1990, 1, 1)
            );

            invalidRegisterRequest = new RegisterRequest(
                "invalid-email",
                "short",
                "",
                "User",
                null,
                null
            );
        }

        @Test
        void testRegisterUser_Success() throws Exception {
            when(authService.registerUser(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("Registration successful! Please verify your email."));
            
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful! Please verify your email."));
        }

        @Test
        void testRegisterUser_EmailAlreadyExists() throws Exception {
            when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already registered: test@examle.com"));
            
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered: test@examle.com"));
        }

        @Test
        void testRegisterUser_InvalidRequest() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRegisterRequest)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SignInRequest_tests {
        private SignInRequest validSignInRequest;
        private SignInRequest invalidSignInRequest;
        
        @BeforeEach
        void setUp() {
            validSignInRequest = new SignInRequest("email@email.com", "Password1");
            invalidSignInRequest = new SignInRequest("email1@email.com", "Password1");
        }

        @Test
        void testSignIn_success() throws Exception {
            when(authService.signIn(any(SignInRequest.class)))
                .thenReturn(new AuthResponse("Sign in successful for user: " + validSignInRequest.getEmail()));

            mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignInRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Sign in successful for user: " + validSignInRequest.getEmail()));
        }

        @Test
        void testSignIn_failIncorrectEmail() throws Exception {

            when(authService.signIn(any(SignInRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));
    
            mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSignInRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid email or password"));
        } 
    }
}
