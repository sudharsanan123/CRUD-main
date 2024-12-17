package com.example.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.spring.model.User;
import com.example.spring.service.TeacherService;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
    @GetMapping("/me")
    public ResponseEntity<User> getLoggedUser(Principal principal) {
        String username = principal.getName(); // Getting logged-in username from JWT token
        User user = teacherService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllTeachers() {
        List<User> teachers = teacherService.getAllTeachers(); // Method to fetch all teachers
        return ResponseEntity.ok(teachers);
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        logger.debug("Received request to update user with ID: {}", id);
        User updatedUser = teacherService.updateUser(id, user);
        logger.info("User with ID {} updated successfully", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        logger.debug("Received request to delete user with ID: {}", id);
        teacherService.deleteUser(id);
        logger.info("User with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
