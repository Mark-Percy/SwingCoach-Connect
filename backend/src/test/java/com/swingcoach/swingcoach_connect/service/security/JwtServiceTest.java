package com.swingcoach.swingcoach_connect.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    
    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    private final String TEST_SECRET = "mnKD-0OejcqHcIBdN8O45oyZ0wf5iLxVwfCrQ1o9adfGRu5CpXLbzSS2FXR1wCf4j3ksMPB3WqnjBOJpNHmKrw==";
    private final long TEST_EXPIRTATION = 3600000;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", TEST_EXPIRTATION);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
    }

    @Test
    void testGenerateToken_success() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testExtractUserName_success() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), username, "Extracted Username successfully");
    }

    @Test
    void testIsTokenValid_success() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }
    
    @Test
    void testIsTokenValid_tokenExpired() throws Exception {
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);
        String token = jwtService.generateToken(userDetails);
        Thread.sleep(2);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_wrongUsername() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUserDetails = mock(UserDetails.class);
        when(otherUserDetails.getUsername()).thenReturn("other@example.com");
        
        boolean isValid = jwtService.isTokenValid(token, otherUserDetails);

        assertFalse(isValid, "Token should be invalid because the username does not match");
    }

    @Test
    void testIsTokenValid_InvalidSignature() {
        String validToken = jwtService.generateToken(userDetails);
        ReflectionTestUtils.setField(jwtService, "secret", "Kv4L65Y7mWcOKfUkI0wWHP0APaplWTVTXHhDLJHDbbJpUKnRkJZR8iVJZXjuuWT3u5iImX4aB8g3lZOpB9Uoow==");
        boolean isValid = jwtService.isTokenValid(validToken, userDetails);

        assertFalse(isValid, "Token should be invalid because of an invalid signature");
        
    }
}
