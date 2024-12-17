package com.example.spring.controller;

import com.example.spring.model.User;
import com.example.spring.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = studentService.getUsers();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getStudentById(@PathVariable int id) {
        return studentService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getLoggedStudent(Principal principal) {
        String username = principal.getName(); 
        User user = studentService.findUserByUsername(username);
        return user != null ? ResponseEntity.ok(user) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<User> addStudent(@RequestBody User student) {
        User savedStudent = studentService.addUser(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateStudent(@PathVariable int id, @RequestBody User student) {
        User updatedStudent = studentService.updateUser(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {
        studentService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
