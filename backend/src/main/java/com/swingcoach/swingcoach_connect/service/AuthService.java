package com.swingcoach.swingcoach_connect.service;

import com.swingcoach.swingcoach_connect.dto.auth.RegisterRequest;
import com.swingcoach.swingcoach_connect.dto.auth.SignInRequest;
import com.swingcoach.swingcoach_connect.dto.auth.AuthResponse;
import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.repository.UserRepository;

import org.springframework.security.core.AuthenticationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAccountStatus(User.AccountStatus.PENDING_EMAIL_VERIFICATION);
        user.setIsEmailVerified(false);

        userRepository.save(user);

        return new AuthResponse("Registration Successful! Please verify your email.");
    }

    public Object registerUser(Class<RegisterRequest> class1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerUser'");
    }

    public AuthResponse signIn(SignInRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            return new AuthResponse("Sign in successful for user: " + request.getEmail());
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
}
