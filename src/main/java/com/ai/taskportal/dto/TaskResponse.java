package com.ai.taskportal.dto;

import com.ai.taskportal.entity.TaskPriority;
import com.ai.taskportal.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {

    private UUID id;

    private UUID userId;

    private String title;

    private String description;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDate dueDate;

    private Boolean deleted;

    private Integer estimatedEffort;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
