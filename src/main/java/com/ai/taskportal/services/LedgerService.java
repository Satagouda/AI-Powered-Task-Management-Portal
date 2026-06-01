package com.ai.taskportal.services;

import com.ai.taskportal.entity.Task;
import com.ai.taskportal.entity.TaskHistory;
import com.ai.taskportal.exception.TaskNotFoundException;
import com.ai.taskportal.repository.TaskHistoryRepository;
import com.ai.taskportal.repository.TaskRepository;
import com.ai.taskportal.util.HashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;

    public List<TaskHistory> getLedger(
            UUID taskId
    ) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found"
                        )
                );

        return taskHistoryRepository
                .findByTaskOrderByBlockIndexAsc(task);
    }

    public boolean verifyChain(
            UUID taskId
    ) {

        log.info(
                "Starting blockchain verification for task {}",
                taskId
        );

        List<TaskHistory> blocks =
                getLedger(taskId);

        if (blocks.isEmpty()) {

            log.info(
                    "No blocks found for task {}. Chain considered valid.",
                    taskId
            );

            return true;
        }

        for (int i = 0; i < blocks.size(); i++) {

            TaskHistory current =
                    blocks.get(i);

            String expectedPreviousHash =
                    i == 0
                            ? "GENESIS"
                            : blocks.get(i - 1)
                            .getCurrentHash();

            log.info("========================================");
            log.info(
                    "Verifying block {}",
                    current.getBlockIndex()
            );

            log.info(
                    "Action: {}",
                    current.getAction()
            );

            log.info(
                    "Expected Previous Hash: {}",
                    expectedPreviousHash
            );

            log.info(
                    "Actual Previous Hash: {}",
                    current.getPreviousHash()
            );

            if (!expectedPreviousHash.equals(
                    current.getPreviousHash())) {

                log.error(
                        "Previous hash mismatch detected at block {}",
                        current.getBlockIndex()
                );

                return false;
            }

//            String recalculatedHash =
//                    HashUtil.sha256(
//                            current.getPreviousHash()
//                                    + current.getPayload()
//                                    + current.getAction()
//                                    + current.getBlockIndex()
//                    );

            String canonicalPayload;

            try {

                canonicalPayload =
                        new ObjectMapper()
                                .writeValueAsString(
                                        new ObjectMapper()
                                                .readTree(
                                                        current.getPayload()
                                                )
                                );

            } catch (Exception ex) {

                log.error(
                        "Failed to canonicalize payload for block {}",
                        current.getBlockIndex(),
                        ex
                );

                return false;
            }

            String recalculatedHash =
                    HashUtil.sha256(
                            current.getPreviousHash()
                                    + canonicalPayload
                                    + current.getAction()
                                    + current.getBlockIndex()
                    );

            log.info(
                    "Payload: {}",
                    current.getPayload()
            );

            log.info(
                    "Stored Hash: {}",
                    current.getCurrentHash()
            );

            log.info(
                    "Calculated Hash: {}",
                    recalculatedHash
            );

            boolean hashMatch =
                    recalculatedHash.equals(
                            current.getCurrentHash()
                    );

            log.info(
                    "Hash Match: {}",
                    hashMatch
            );

            log.info("========================================");

            if (!hashMatch) {

                log.error(
                        "Hash validation failed for block {}",
                        current.getBlockIndex()
                );

                return false;
            }
        }

        log.info(
                "Blockchain verification successful for task {}",
                taskId
        );

        return true;
    }
}