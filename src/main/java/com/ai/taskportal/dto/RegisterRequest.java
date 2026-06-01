package com.ai.taskportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private String firstName;

    private String lastName;
}