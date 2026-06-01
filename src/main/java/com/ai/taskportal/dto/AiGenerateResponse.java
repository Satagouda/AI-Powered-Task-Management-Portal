package com.ai.taskportal.dto;

import com.ai.taskportal.entity.TaskPriority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiGenerateResponse {

    private String description;
    private TaskPriority priority;
    private Integer estimatedEffort;
}
