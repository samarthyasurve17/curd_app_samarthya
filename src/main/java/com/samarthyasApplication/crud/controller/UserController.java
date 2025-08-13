package com.samarthyasApplication.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.samarthyasApplication.crud.model.User;
import com.samarthyasApplication.crud.services.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
public class UserController {

    @Autowired
    private UserService userService;

    // Create user
    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        return this.userService.addUser(user);
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getUsers() {
        return this.userService.getUsers();
    }

    // Get single user
    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable String userId) {
        return this.userService.getUser(Long.parseLong(userId));
    }

    // Update user
    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User user) {
        user.setId(userId);  // Ensures correct ID is updated
        return this.userService.updateUser(user);
    }

    // Delete user
    @DeleteMapping("/users/{userId}")
    public User deleteUser(@PathVariable String userId) {
        return this.userService.deleteUser(Long.parseLong(userId));
    }
}
