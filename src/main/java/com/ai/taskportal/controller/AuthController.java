package com.ai.taskportal.controller;

import com.ai.taskportal.dto.*;
import com.ai.taskportal.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build();
    }
}
