package com.ai.taskportal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private boolean success;

    private String message;

    private LocalDateTime timestamp;
}