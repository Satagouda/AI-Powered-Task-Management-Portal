package com.ai.taskportal.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryResponse {

    private UUID id;
    private UUID taskId;
    private String action;
    private JsonNode payload;    // deserialized from the stored JSON string
    private String previousHash;
    private String currentHash;
    private Integer blockIndex;
    private LocalDateTime createdAt;
}