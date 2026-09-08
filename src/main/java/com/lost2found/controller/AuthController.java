package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.dto.JwtResponse;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.dto.UserProfileResponse;
import com.lost2found.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing User Registration and Login endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserProfileResponse userProfile = authService.registerUser(registerRequest);
        return new ResponseEntity<>(
                ApiResponse.created("User registered successfully", userProfile),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(
                ApiResponse.success("User logged in successfully", jwtResponse)
        );
    }
}
