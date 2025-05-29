package com.rca.ne.controller;

import com.rca.ne.dto.auth.JwtResponse;
import com.rca.ne.dto.auth.LoginRequest;
import com.rca.ne.dto.auth.RegisterRequest;
import com.rca.ne.model.Employee;
import com.rca.ne.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user and return user details")
    public ResponseEntity<Employee> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Employee employee = authService.registerUser(registerRequest);
        return ResponseEntity.ok(employee);
    }
}