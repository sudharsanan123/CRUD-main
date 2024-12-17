package com.example.spring.service;
import com.example.spring.model.User;
import com.example.spring.repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private UserRepo userRepo;

    // Method for Management to add a User (User role cannot access)
    public User addUser(User User) {
        if (userRepo.existsByUsername(User.getUsername())) {
            throw new IllegalArgumentException("Duplicate User name: " + User.getUsername());
        }
        return userRepo.save(User);
    }

    public User findUserByUsername(String username) {
        Optional<User> UserOptional = userRepo.findByUsername(username);
        return UserOptional.orElse(null);  // Return null if User not found
    }
    

    // Method to get all Users - accessible to MANAGEMENT, TEACHER
   public List<User> getUsers() {
        return userRepo.findAll().stream()
                .filter(user -> "STUDENT".equals(user.getRole().name()))
                .collect(Collectors.toList());
    }

    // Method to update a User - accessible to MANAGEMENT and TEACHER only
    public User updateUser(int id, User updatedUser) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("User with id " + id + " not found.");
        }

        if (userRepo.existsByUsername(updatedUser.getUsername()) && userRepo.findByUsername(updatedUser.getUsername()).get().getId() != id) {
            throw new IllegalArgumentException("Duplicate User name: " + updatedUser.getUsername());
        }

        updatedUser.setId(id);
        return userRepo.save(updatedUser);
    }

    // Method to delete a User - accessible to MANAGEMENT only
    public boolean deleteUser(int id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("User with id " + id + " not found.");
        }
        userRepo.deleteById(id);
        return true;
    }

    // Method to get a User by ID - accessible to MANAGEMENT, TEACHER, and the User themselves
    public Optional<User> getUserById(int id) {
        return userRepo.findById(id);
    }
}

