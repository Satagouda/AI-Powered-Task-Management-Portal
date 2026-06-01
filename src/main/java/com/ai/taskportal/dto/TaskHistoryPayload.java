package com.ai.taskportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryPayload {

    private UUID taskId;
    private UUID userId;         // only the ID, never the User object

    private String title;
    private String description;
    private String priority;
    private String status;

    private LocalDate dueDate;
    private Integer estimatedEffort;
    private Boolean deleted;

    private LocalDateTime taskCreatedAt;
    private LocalDateTime taskUpdatedAt;
}
