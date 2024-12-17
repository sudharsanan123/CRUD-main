package com.example.spring.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.spring.config.Role;
import com.example.spring.model.User; 
import com.example.spring.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
             
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User userRequest) {
        try {
            User registeredUser = userService.registerUser(userRequest);
            logger.info("User registered successfully: {}", registeredUser.getUsername());
    
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully.");
            response.put("username", registeredUser.getUsername());
            response.put("role", registeredUser.getRole().name());
            response.put("email", registeredUser.getEmail());
    
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Error registering user {}: {}", userRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User userRequest) {
        try {
            // Extract the expected role from the userRequest, assuming it's sent in the request
            Role expectedRole = userRequest.getRole(); 
            
            // Call the verifyUser method with the username, password, and expected role
            String token = userService.verifyUser(userRequest.getUsername(), userRequest.getPassword(), expectedRole);
            
            logger.info("User {} logged in successfully.", userRequest.getUsername());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            logger.error("Login error for user {}: {}", userRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    
}