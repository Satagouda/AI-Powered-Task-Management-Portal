package com.ai.taskportal.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDTO {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Boolean isEmailVerified;
}
