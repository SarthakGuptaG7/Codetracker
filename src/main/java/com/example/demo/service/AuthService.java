package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        validatePassword(request.getPassword());
        String email = normalize(request.getEmail());
        String username = normalize(request.getUsername());
        if (username == null || username.isBlank()) {
            username = email;
        }
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (repository.existsByUsername(username) || repository.existsByEmail(email)) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName() != null && !request.getDisplayName().isBlank() ? request.getDisplayName().trim() : username);
        
        try {
            if (request.getRole() != null) {
                user.setRole(com.example.demo.model.Role.valueOf(request.getRole().toUpperCase()));
            }
        } catch (IllegalArgumentException e) {
            user.setRole(com.example.demo.model.Role.STUDENT);
        }
        
        repository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    private void validatePassword(String password) {
        if (password == null
                || password.length() < 8
                || !password.matches(".*[a-z].*")
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[^A-Za-z0-9].*")) {
            throw new RuntimeException("Password must be at least 8 characters and include uppercase, lowercase, number, and special character");
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
        String identifier = normalize(request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        request.getPassword()
                )
        );
        User user = repository.findByEmail(identifier)
                .or(() -> repository.findByUsername(identifier))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    public User getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByUsername(username).orElseThrow();
    }

    public void updateRole(String roleName) {
        User user = getCurrentUser();
        try {
            user.setRole(com.example.demo.model.Role.valueOf(roleName.toUpperCase()));
            repository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role");
        }
    }

    public void updateCredentials(com.example.demo.dto.CredentialsUpdateRequest request) {
        User user = getCurrentUser();
        user.setLeetcodeUsername(request.getLeetcodeUsername());
        user.setHackerrankUsername(request.getHackerrankUsername());
        user.setGeeksforgeeksUsername(request.getGeeksforgeeksUsername());
        repository.save(user);
    }
}
