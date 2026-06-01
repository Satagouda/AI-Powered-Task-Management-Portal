package com.ai.taskportal.services;

import com.ai.taskportal.dto.TaskHistoryResponse;
import com.ai.taskportal.dto.TaskRequest;
import com.ai.taskportal.dto.TaskResponse;
import com.ai.taskportal.entity.*;
import com.ai.taskportal.repository.TaskHistoryRepository;
import com.ai.taskportal.repository.TaskRepository;
import com.ai.taskportal.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final BlockchainService blockchainService;
    private final ObjectMapper objectMapper;

    // ─── Create ───────────────────────────────────────────────────────────────

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        User user = getCurrentUser();

        Task task = Task.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() == null
                        ? TaskPriority.MEDIUM : request.getPriority())
                .status(request.getStatus() == null
                        ? TaskStatus.TODO : request.getStatus())
                .dueDate(request.getDueDate())
                .estimatedEffort(request.getEstimatedEffort())
                .deleted(false)
                .build();

        Task savedTask = taskRepository.save(task);
        blockchainService.createBlock(savedTask, "CREATE");

        return mapTask(savedTask);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID taskId) {
        Task task = validateOwnership(taskId);
        return mapTask(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        User currentUser = getCurrentUser();

        return taskRepository.findByUserAndDeletedFalse(currentUser)
                .stream()
                .map(this::mapTask)
                .collect(Collectors.toList());
    }

    // ─── History ──────────────────────────────────────────────────────────────

//    @Transactional(readOnly = true)
//    public List<TaskHistoryResponse> getTaskHistory(UUID taskId) {
//
//        // Ownership check — ensures user owns the task
//        validateOwnership(taskId);
//
//        // JOIN FETCH already loaded — no lazy proxy issues
//        List<TaskHistory> history =
//                historyRepository.findAllByTaskIdWithTask(taskId);
//
//        return history.stream()
//                .map(this::mapHistory)
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<TaskHistoryResponse> getTaskHistory(UUID taskId) {
        Task task = validateOwnership(taskId);
        return historyRepository.findByTaskOrderByBlockIndexAsc(task)
                .stream()
                .map(h -> mapHistory(h, taskId))
                .collect(Collectors.toList());
    }
    // ─── Update ───────────────────────────────────────────────────────────────

    @Transactional
    public TaskResponse updateTask(UUID taskId, TaskRequest request) {
        Task task = validateOwnership(taskId);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setEstimatedEffort(request.getEstimatedEffort());

        Task updatedTask = taskRepository.save(task);
        blockchainService.createBlock(updatedTask, "UPDATE");

        return mapTask(updatedTask);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Transactional
    public void deleteTask(UUID taskId) {
        Task task = validateOwnership(taskId);
        task.setDeleted(true);
        Task deletedTask = taskRepository.save(task);
        blockchainService.createBlock(deletedTask, "DELETE");
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    /**
     * Validates task exists and belongs to the current user.
     * Called inside @Transactional — lazy loads are safe here.
     */
    private Task validateOwnership(UUID taskId) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        // task.getUser() lazy load — safe because we're inside @Transactional
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied to task: " + taskId);
        }

        return task;
    }

    /**
     * Gets the authenticated user from Spring Security context.
     * Primary: cast principal directly (User entity — zero extra DB call).
     * Fallback: look up by email from authentication name.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user in security context");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            log.debug("Resolved current user via principal cast: {}", user.getEmail());
            return user;
        }

        // Fallback: User.getUsername() returns email → getName() returns email
        String email = authentication.getName();
        log.debug("Resolved current user via email lookup: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }


    private TaskResponse mapTask(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .userId(task.getUser() != null ? task.getUser().getId() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .estimatedEffort(task.getEstimatedEffort())
                .deleted(task.getDeleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

//    private TaskHistoryResponse mapHistory(TaskHistory history) {
//        Task task = history.getTask();
//        JsonNode payloadNode = null;
//        try {
//            if (history.getPayload() != null) {
//                payloadNode = objectMapper.readTree(history.getPayload());
//            }
//        } catch (Exception e) {
//            log.warn("Could not parse payload JSON for history block {}: {}",
//                    history.getId(), e.getMessage());
//        }
//
//        return TaskHistoryResponse.builder()
//                .id(history.getId())
//                .taskId(task.getId()) // safe: JOIN FETCH already loaded task
//                .action(history.getAction())
//                .payload(payloadNode)
//                .previousHash(history.getPreviousHash())
//                .currentHash(history.getCurrentHash())
//                .blockIndex(history.getBlockIndex())
//                .createdAt(history.getCreatedAt())
//                .build();
//    }
private TaskHistoryResponse mapHistory(TaskHistory history, UUID taskId) {
    JsonNode payloadNode = null;
    try {
        if (history.getPayload() != null) {
            payloadNode = objectMapper.readTree(history.getPayload());
        }
    } catch (Exception e) {
        log.warn("Could not parse payload for block {}: {}", history.getId(), e.getMessage());
    }

    return TaskHistoryResponse.builder()
            .id(history.getId())
            .taskId(taskId)            // ← was: history.getTask().getId()
            .action(history.getAction())
            .payload(payloadNode)
            .previousHash(history.getPreviousHash())
            .currentHash(history.getCurrentHash())
            .blockIndex(history.getBlockIndex())
            .createdAt(history.getCreatedAt())
            .build();
}

    @Transactional(readOnly = true)      //  was missing @Transactional
    public Page<TaskResponse> searchTasks(
            String keyword,
            TaskStatus status,
            TaskPriority priority,
            int page,
            int size
    ) {
        User user = getCurrentUser();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        if (keyword != null && !keyword.isBlank()) {
            return taskRepository
                    .findByUserAndTitleContainingIgnoreCaseAndDeletedFalse(
                            user, keyword, pageable)
                    .map(this::mapTask);
        }

        if (status != null && priority != null) {
            return taskRepository
                    .findByUserAndStatusAndPriorityAndDeletedFalse(
                            user, status, priority, pageable)
                    .map(this::mapTask);
        }

        if (status != null) {
            return taskRepository
                    .findByUserAndStatusAndDeletedFalse(
                            user, status, pageable)
                    .map(this::mapTask);
        }

        if (priority != null) {
            return taskRepository
                    .findByUserAndPriorityAndDeletedFalse(
                            user, priority, pageable)
                    .map(this::mapTask);    // ✅ was this::map
        }

        // ✅ Uses the Page overload — pageable version
        return taskRepository
                .findByUserAndDeletedFalse(user, pageable)
                .map(this::mapTask);
    }

    public TaskResponse updateTaskStatus(
            UUID taskId,
            TaskStatus status
    ) {

        Task task = validateOwnership(taskId);

        task.setStatus(status);

        Task updatedTask = taskRepository.save(task);

        blockchainService.createBlock(
                updatedTask,
                "STATUS_UPDATED"
        );

        return mapTask(updatedTask);
    }

}

