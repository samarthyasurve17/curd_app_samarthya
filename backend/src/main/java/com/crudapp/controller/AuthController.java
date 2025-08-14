package com.crudapp.controller;

import com.crudapp.dao.UserDAO;
import com.crudapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserDAO userDAO;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, 
                                   @RequestParam String password,
                                   HttpSession session) {
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            responseMap.put("success", false);
            responseMap.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(responseMap);
        }
        
        if (userDAO.authenticateUser(username.trim(), password.trim())) {
            User user = userDAO.getUserByUsername(username.trim());
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            
            responseMap.put("success", true);
            responseMap.put("message", "Login successful");
            responseMap.put("user", user);
            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("success", false);
            responseMap.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(responseMap);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                     @RequestParam String password,
                                     @RequestParam String email,
                                     @RequestParam String fullName) {
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (username == null || password == null || email == null || fullName == null ||
            username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || fullName.trim().isEmpty()) {
            responseMap.put("success", false);
            responseMap.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(responseMap);
        }
        
        try {
            User newUser = new User(username.trim(), password.trim(), email.trim(), fullName.trim());
            User savedUser = userDAO.createUser(newUser);
            
            responseMap.put("success", true);
            responseMap.put("message", "User registered successfully");
            responseMap.put("userId", savedUser.getId());
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            responseMap.put("success", false);
            responseMap.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("message", "Logout successful");
        return ResponseEntity.ok(responseMap);
    }
}
