package com.example.spring.controller;

import com.example.spring.config.Role; // Ensure Role is imported
import com.example.spring.model.User;
import com.example.spring.service.ManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    // Endpoint to get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = managementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Endpoint to get users by role
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase()); // Convert string to Role enum
            List<User> users = managementService.getUsersByRole(userRole); // Call the service with Role
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return a bad request with null body for invalid role
        }
    }

    // Endpoint to register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            Role role = user.getRole(); // Assuming Role is already set in User
            managementService.addUser(user, role); // Pass Role to service
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint to update a user
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User userDetails) {
        User updatedUser = managementService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Endpoint to delete a user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        managementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
}
