package com.ai.taskportal.repository;


import com.ai.taskportal.entity.Task;
import com.ai.taskportal.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskHistoryRepository
        extends JpaRepository<TaskHistory, UUID> {
    List<TaskHistory> findByTaskOrderByBlockIndexAsc(
            Task task
    );

    Optional<TaskHistory> findTopByTaskOrderByBlockIndexDesc(Task task);

}
