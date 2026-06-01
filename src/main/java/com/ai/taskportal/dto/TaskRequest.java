package com.ai.taskportal.dto;


import com.ai.taskportal.entity.TaskPriority;
import com.ai.taskportal.entity.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDate dueDate;

    @Min(1)
    @Max(10)
    private Integer estimatedEffort;
}
