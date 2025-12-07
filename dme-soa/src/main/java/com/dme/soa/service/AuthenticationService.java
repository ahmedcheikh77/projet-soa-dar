package com.dme.soa.service;

import com.dme.persistence.entity.User;
import com.dme.persistence.repository.UserRepository;
import com.dme.infrastructure.security.JwtTokenProvider;
import com.dme.infrastructure.security.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EncryptionService encryptionService;

    public String authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null || !user.getActive()) {
            log.warn("Authentication failed for user: {}", username);
            return null;
        }

        // Verify password
        if (!encryptionService.verifyPassword(password, user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", username);
            return null;
        }

        String role = user.getRole().toString();
        String token = jwtTokenProvider.generateToken(username, role);
        log.info("User authenticated successfully: {}", username);
        
        return token;
    }

    public User registerUser(String username, String email, String password, String fullName, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("Username already exists: {}", username);
            return null;
        }

        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Email already exists: {}", email);
            return null;
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
        
        return savedUser;
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
