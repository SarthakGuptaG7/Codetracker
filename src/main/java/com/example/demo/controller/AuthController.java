package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RoleUpdateRequest;
import com.example.demo.dto.CredentialsUpdateRequest;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PutMapping("/role")
    public ResponseEntity<String> updateRole(@RequestBody RoleUpdateRequest request) {
        authService.updateRole(request.getRole());
        return ResponseEntity.ok("Role updated successfully");
    }

    @PutMapping("/credentials")
    public ResponseEntity<String> updateCredentials(@RequestBody CredentialsUpdateRequest request) {
        authService.updateCredentials(request);
        return ResponseEntity.ok("Credentials updated successfully");
    }
}
