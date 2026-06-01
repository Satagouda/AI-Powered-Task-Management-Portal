package com.ai.taskportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiGenerateRequest {

    @NotBlank
    private String title;
}
