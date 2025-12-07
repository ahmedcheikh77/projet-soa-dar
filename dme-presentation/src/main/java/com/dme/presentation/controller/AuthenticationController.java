package com.dme.presentation.controller;

import com.dme.persistence.entity.User;
import com.dme.persistence.repository.UserRepository;
import com.dme.infrastructure.security.JwtTokenProvider;
import com.dme.infrastructure.security.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null || !user.getActive()) {
            log.warn("Authentication failed for user: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        if (!encryptionService.verifyPassword(password, user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        String role = user.getRole().toString();
        String token = jwtTokenProvider.generateToken(username, role);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("role", role);
        response.put("message", "Login successful");

        log.info("User logged in: {}", username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String fullName = request.get("fullName");
        String role = request.getOrDefault("role", "PATIENT");

        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("Username already exists: {}", username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Username already exists"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Email already exists: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email already exists"));
        }

        String hashedPassword = encryptionService.hashPassword(password);
        
        User newUser = User.builder()
                .username(username)
                .email(email)
                .passwordHash(hashedPassword)
                .fullName(fullName)
                .role(com.dme.persistence.entity.UserRole.valueOf(role.toUpperCase()))
                .active(true)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("New user registered: {}", username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "id", savedUser.getId(),
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail(),
                    "message", "User registered successfully"
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("User logged out");
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
