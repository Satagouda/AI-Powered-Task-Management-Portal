package com.ai.taskportal.services;

import com.ai.taskportal.dto.*;
import com.ai.taskportal.entity.Role;
import com.ai.taskportal.entity.User;
import com.ai.taskportal.repository.RoleRepository;
import com.ai.taskportal.repository.UserRepository;
import com.ai.taskportal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new RuntimeException("ROLE_USER not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        user = userRepository.save(user);

        String accessToken =
                jwtTokenProvider.generateAccessToken(user.getEmail());

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user.getEmail());

        return buildResponse(user, accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String accessToken =
                jwtTokenProvider.generateAccessToken(user.getEmail());

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user.getEmail());

        return buildResponse(user, accessToken, refreshToken);
    }

    private AuthResponse buildResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(
                        UserDTO.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .isEmailVerified(user.getIsEmailVerified())
                                .build()
                )
                .build();
    }
}