package com.example.spring.service;

// import com.example.spring.model.User;
import com.example.spring.model.User;  // Assuming Users is your user model
import com.example.spring.repo.UserRepo;  // Change to UserRepo
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;  // Use UserRepo instead of UserRepo
    private final PasswordEncoder passwordEncoder;

    public TeacherService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User User) {
        LOG.debug("UserService.register() => Attempting to register User: {}", User.getUsername());
        
        if (userRepo.existsByUsername(User.getUsername())) { // Use UserRepo to check existence
            LOG.error("UserService.register() => Duplicate User username: {}", User.getUsername());
            throw new IllegalArgumentException("Duplicate User username: " + User.getUsername());
        }

        User.setPassword(passwordEncoder.encode(User.getPassword()));
        LOG.info("UserService.register() => User registered successfully: {}", User.getUsername());
        return userRepo.save(User);  // Save through UserRepo
    }
    public List<User> getAllTeachers() {
            return userRepo.findAll().stream()
            .filter(user -> "TEACHER".equals(user.getRole().name())) // Filter by TEACHER role
            .collect(Collectors.toList());
    }

    public Optional<User> getUserById(int id) {
        LOG.debug("UserService.getUserById() => Fetching User by ID: {}", id);
        Optional<User> userOptional = userRepo.findById(id); // Fetch from UserRepo

        if (userOptional.isPresent() && userOptional.get() instanceof User) {
            User User = (User) userOptional.get();
            LOG.info("UserService.getUserById() => User found: {}", User.getUsername());
            return Optional.of(User);
        } else {
            LOG.warn("UserService.getUserById() => User not found with ID: {}", id);
            return Optional.empty();
        }
    }

    public User findUserByUsername(String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get() instanceof User) {
            return (User) userOptional.get(); // Cast to User
        }
        return null;  // Return null if User not found
    }

    public User updateUser(int id, User User) {
        LOG.debug("UserService.updateUser() => Attempting to update User with ID: {}", id);
        Optional<User> existingUserOptional = userRepo.findById(id);

        if (!existingUserOptional.isPresent() || !(existingUserOptional.get() instanceof User)) {
            LOG.error("UserService.updateUser() => User not found for update with ID: {}", id);
            throw new IllegalArgumentException("User not found.");
        }

        User existingUser = (User) existingUserOptional.get();
        if (userRepo.existsByUsername(User.getUsername()) && 
            !existingUser.getUsername().equals(User.getUsername())) {
            LOG.error("UserService.updateUser() => Duplicate User username during update: {}", User.getUsername());
            throw new IllegalArgumentException("Duplicate User username.");
        }

        if (User.getPassword() != null) {
            LOG.debug("UserService.updateUser() => Encoding password for User: {}", User.getUsername());
            User.setPassword(passwordEncoder.encode(User.getPassword()));
        }

        User.setId(id);
        LOG.info("UserService.updateUser() => User updated successfully: {}", User.getUsername());
        return userRepo.save(User);  // Save through UserRepo
    }

    public void deleteUser(int id) {
        LOG.debug("UserService.deleteUser() => Attempting to delete User with ID: {}", id);
        if (!userRepo.existsById(id)) {
            LOG.error("UserService.deleteUser() => User not found for deletion with ID: {}", id);
            throw new IllegalArgumentException("User not found.");
        }
        userRepo.deleteById(id);  // Delete through UserRepo
        LOG.info("UserService.deleteUser() => User with ID {} deleted successfully.", id);
    }
}
