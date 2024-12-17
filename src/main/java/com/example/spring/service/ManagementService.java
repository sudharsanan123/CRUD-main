package com.example.spring.service;

import com.example.spring.config.Role;
import com.example.spring.model.User;
import com.example.spring.repo.UserRepo;
import com.example.spring.model.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagementService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public ManagementService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<User> getAllUsersByRole(Role role) { // Changed to accept Role
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.MANAGEMENT) {
            throw new RuntimeException("Access denied. Only management can view users by role.");
        }
        return userRepo.findByRole(role); // Ensure the repo method accepts Role
    }

    public User addUser(User newUser, Role role) { // Changed to accept Role
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.MANAGEMENT) {
            throw new RuntimeException("Access denied. Only management can add users.");
        }

        newUser.setRole(role); // Set the role directly

        // Ensure username and email uniqueness
        if (userRepo.existsByUsername(newUser.getUsername())) {
            throw new RuntimeException("Username already exists.");
        }

        if (userRepo.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        // Encode the password
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        return userRepo.save(newUser);
    }

    @Transactional
    public User updateUser(int id, User userDetails) {
        User currentUser = getCurrentUser();
        User userToUpdate = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Only MANAGEMENT can update any user's details
        if (currentUser.getRole() != Role.MANAGEMENT) {
            throw new RuntimeException("Access denied. Only management can update user details.");
        }

        // Update fields
        userToUpdate.setUsername(userDetails.getUsername());
        userToUpdate.setEmail(userDetails.getEmail());

        return userRepo.save(userToUpdate);
    }

    @Transactional
    public void deleteUser(int id) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.MANAGEMENT) {
            throw new RuntimeException("Access denied. Only management can delete users.");
        }

        userRepo.deleteById(id);
    }

    public List<User> getUsersByRole(Role role) { // Changed to accept Role
        List<User> allUsers = getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRole() == role) // Adjust comparison to use Role enum
                .collect(Collectors.toList());
    }
}
