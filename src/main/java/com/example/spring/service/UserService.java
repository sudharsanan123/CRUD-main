package com.example.spring.service;

import com.example.spring.config.Role;
import com.example.spring.model.User;
import com.example.spring.repo.UserRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private JWTService jwtService;

    public User registerUser(User userRequest) {
        String normalizedUsername = userRequest.getUsername().toLowerCase();
        LOG.debug("UserService.registerUser() => Normalized username for registration: {}", normalizedUsername);

        if (userRepo.existsByUsername(normalizedUsername)) {
            LOG.warn("UserService.registerUser() => Registration failed: User with username {} already exists.", normalizedUsername);
            throw new RuntimeException("User already exists");
        }

        try {
            userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userRepo.save(userRequest);
            
            // Debug log to check the saved user's details
            LOG.debug("User registered with details: {}", userRequest);
            
            LOG.info("UserService.registerUser() => User {} registered successfully.", normalizedUsername);
            return userRequest;
        } catch (Exception e) {
            LOG.error("UserService.registerUser() => Error while registering user {}: {}", normalizedUsername, e.getMessage());
            throw new RuntimeException("Error registering user");
        }
    }
    
    public String verifyUser(String username, String password, Role requiredRole) {
        LOG.info("UserService.verifyUser() => Authenticating user: {}", username);
    
        // Normalize the username for consistency
        String normalizedUsername = username.toLowerCase();
    
        User existingUser = userRepo.findByUsername(normalizedUsername)
                .orElseThrow(() -> {
                    LOG.warn("UserService.verifyUser() => User not found: {}", normalizedUsername);
                    return new RuntimeException("User not found");
                });
    
        // Verify password
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            LOG.warn("UserService.verifyUser() => Invalid credentials for user: {}", normalizedUsername);
            throw new RuntimeException("Invalid username or password");
        }
    
        // Authenticate user
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(existingUser.getUsername(), password));
    
        if (authentication.isAuthenticated()) {
            // Check if the user's role matches the required role
            if (!existingUser.getRole().equals(requiredRole)) {
                LOG.warn("UserService.verifyUser() => Role mismatch for user: {}", normalizedUsername);
                throw new RuntimeException("Role mismatch");
            }
    
            String token = jwtService.generateToken(existingUser.getUsername(), existingUser.getRole());
            LOG.info("UserService.verifyUser() => Token generated for user: {}", normalizedUsername);
            return token;
        }
    
        LOG.warn("UserService.verifyUser() => Authentication failed for user: {}", normalizedUsername);
        return "fail";
    }
    
}
