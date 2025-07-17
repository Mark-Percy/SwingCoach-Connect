package com.swingcoach.swingcoach_connect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swingcoach.swingcoach_connect.dto.auth.AuthResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    // Test controller to check authenticated users.
    @RequestMapping("/getUser")
    public ResponseEntity<AuthResponse> getUser() {
        return new ResponseEntity<>(new AuthResponse("user Retrieved", null), HttpStatus.OK);
    }
    
}
