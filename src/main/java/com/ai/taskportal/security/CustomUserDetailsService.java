package com.ai.taskportal.security;


import com.ai.taskportal.entity.User;
import com.ai.taskportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by email
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", email);

                    return new UsernameNotFoundException(
                            "User not found with email: " + email
                    );
                });

        log.debug(
                "User loaded successfully: {}",
                user.getEmail()
        );

        return user;
    }

    /**
     * Load user by ID
     */
    public UserDetails loadUserById(UUID userId)
            throws UsernameNotFoundException {

        log.debug("Loading user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(
                            "User not found with ID: {}",
                            userId
                    );

                    return new UsernameNotFoundException(
                            "User not found with ID: " + userId
                    );
                });

        return user;
    }
}