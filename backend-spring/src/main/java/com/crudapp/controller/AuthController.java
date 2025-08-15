package com.crudapp.controller;

import com.crudapp.model.User;
import com.crudapp.repository.UserRepository;
import com.crudapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username.trim(), password.trim()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();

            // Store in session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String fullName) {

        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null || email == null || fullName == null ||
                username.trim().isEmpty() || password.trim().isEmpty() || 
                email.trim().isEmpty() || fullName.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        username = username.trim();
        email = email.trim();
        fullName = fullName.trim();

        if (userRepository.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            User newUser = new User(username, passwordEncoder.encode(password.trim()), email, fullName);
            User savedUser = userService.saveUser(newUser);

            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to register user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (session != null) {
            session.invalidate();
            SecurityContextHolder.clearContext();
            response.put("success", true);
            response.put("message", "Logout successful");
        } else {
            response.put("success", false);
            response.put("message", "No active session");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuthStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            response.put("success", true);
            response.put("authenticated", true);
            response.put("user", user);
        } else {
            response.put("success", true);
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }
}