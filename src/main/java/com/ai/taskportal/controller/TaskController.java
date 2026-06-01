package com.ai.taskportal.controller;



import com.ai.taskportal.dto.*;
import com.ai.taskportal.entity.TaskPriority;
import com.ai.taskportal.entity.TaskStatus;
import com.ai.taskportal.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task")
    public ApiResponse<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request) {

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task created successfully")
                .data(taskService.createTask(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all tasks for current user")
    public ApiResponse<List<TaskResponse>> getAllTasks() {

        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Tasks fetched successfully")
                .data(taskService.getAllTasks())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ApiResponse<TaskResponse> getTask(
            @PathVariable UUID id) {

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task fetched successfully")
                .data(taskService.getTaskById(id))
                .build();
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get blockchain history for a task")
    public ApiResponse<List<TaskHistoryResponse>> getHistory(
            @PathVariable UUID id) {

        return ApiResponse.<List<TaskHistoryResponse>>builder()
                .success(true)
                .message("Task history fetched successfully")
                .data(taskService.getTaskHistory(id)) // service maps to DTOs
                .build();
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequest request) {

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task updated successfully")
                .data(taskService.updateTask(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a task")
    public ApiResponse<Void> deleteTask(
            @PathVariable UUID id) {

        taskService.deleteTask(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Task deleted successfully")
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter tasks")
    public ApiResponse<Page<TaskResponse>> searchTasks(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            TaskStatus status,

            @RequestParam(required = false)
            TaskPriority priority,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ApiResponse.<Page<TaskResponse>>builder()
                .success(true)
                .message("Tasks fetched successfully")
                .data(
                        taskService.searchTasks(
                                keyword,
                                status,
                                priority,
                                page,
                                size
                        )
                )
                .build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ApiResponse<TaskResponse> updateTaskStatus(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            TaskStatusUpdateRequest request
    ) {

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task status updated successfully")
                .data(
                        taskService.updateTaskStatus(
                                id,
                                request.getStatus()
                        )
                )
                .build();
    }
}


