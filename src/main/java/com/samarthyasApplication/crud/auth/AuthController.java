package com.samarthyasApplication.crud.auth;

import com.samarthyasApplication.crud.model.AppUser;
import com.samarthyasApplication.crud.dao.AppUserRepository;
import com.samarthyasApplication.crud.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;
    private final AppUserRepository repo;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager,
                          PasswordEncoder encoder,
                          AppUserRepository repo,
                          JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.encoder = encoder;
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        if (repo.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        try {
            String username = creds.get("username");
            String password = creds.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }

            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
