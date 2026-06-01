package com.ai.taskportal.repository;

import com.ai.taskportal.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository
        extends JpaRepository<Task, UUID> {
    List<Task> findByUserAndDeletedFalse(User user);
    Page<Task> findByUserAndDeletedFalse(User user, Pageable pageable);

    Page<Task> findByUserAndStatusAndDeletedFalse(
            User user,
            TaskStatus status,
            Pageable pageable
    );

    Page<Task> findByUserAndPriorityAndDeletedFalse(
            User user,
            TaskPriority priority,
            Pageable pageable
    );

    Page<Task> findByUserAndTitleContainingIgnoreCaseAndDeletedFalse(
            User user,
            String keyword,
            Pageable pageable
    );

    Page<Task> findByUserAndStatusAndPriorityAndDeletedFalse(
            User user,
            TaskStatus status,
            TaskPriority priority,
            Pageable pageable
    );
}