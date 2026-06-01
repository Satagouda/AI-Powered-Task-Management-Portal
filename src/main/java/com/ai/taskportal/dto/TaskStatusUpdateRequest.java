package com.ai.taskportal.dto;


import com.ai.taskportal.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateRequest {

    @NotNull
    private TaskStatus status;
}
