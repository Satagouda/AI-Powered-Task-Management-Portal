package com.ai.taskportal.services;


import com.ai.taskportal.dto.TaskHistoryPayload;
import com.ai.taskportal.entity.Task;
import com.ai.taskportal.entity.TaskHistory;
import com.ai.taskportal.repository.TaskHistoryRepository;
import com.ai.taskportal.util.HashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final TaskHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

//    public void createBlock(Task task, String action) {
//        try {
//            // Build a safe payload DTO — no circular refs, no passwords
//            TaskHistoryPayload payloadDto = buildPayload(task);
//
//            //  Serialize to JSON string — we control exactly what's stored
//            String payloadJson = objectMapper.writeValueAsString(payloadDto);
//
//            //  Find the previous block for chaining
//            TaskHistory previousBlock = historyRepository
//                    .findTopByTaskOrderByBlockIndexDesc(task)
//                    .orElse(null);
//
//            String previousHash = previousBlock == null
//                    ? "GENESIS"
//                    : previousBlock.getCurrentHash();
//
//            int blockIndex = previousBlock == null
//                    ? 0
//                    : previousBlock.getBlockIndex() + 1;
//
//            // Hash uses the JSON string — deterministic and consistent
//            String currentHash = HashUtil.sha256(
//                    previousHash + payloadJson + action + blockIndex
//            );
//
//            TaskHistory block = TaskHistory.builder()
//                    .task(task)
//                    .action(action)
//                    .payload(payloadJson)   // String → stored as jsonb via ::jsonb cast
//                    .previousHash(previousHash)
//                    .currentHash(currentHash)
//                    .blockIndex(blockIndex)
//                    .build();
//
//            historyRepository.save(block);
//
//            log.debug("Block #{} created for task {} — action: {}, hash: {}",
//                    blockIndex, task.getId(), action, currentHash);
//
//        } catch (Exception ex) {
//            log.error("Failed to create blockchain entry for task {}: {}",
//                    task.getId(), ex.getMessage(), ex);
//            throw new RuntimeException("Failed to create blockchain entry", ex);
//        }
//    }

    public void createBlock(Task task, String action) {

        try {

            TaskHistoryPayload payloadDto =
                    buildPayload(task);

            String payloadJson =
                    objectMapper.writeValueAsString(
                            payloadDto
                    );

            // Canonical JSON used for hashing
            String canonicalPayload =
                    objectMapper.writeValueAsString(
                            objectMapper.readTree(payloadJson)
                    );

            TaskHistory previousBlock =
                    historyRepository
                            .findTopByTaskOrderByBlockIndexDesc(task)
                            .orElse(null);

            String previousHash =
                    previousBlock == null
                            ? "GENESIS"
                            : previousBlock.getCurrentHash();

            int blockIndex =
                    previousBlock == null
                            ? 0
                            : previousBlock.getBlockIndex() + 1;

            String currentHash =
                    HashUtil.sha256(
                            previousHash
                                    + canonicalPayload
                                    + action
                                    + blockIndex
                    );


            TaskHistory block =
                    TaskHistory.builder()
                            .task(task)
                            .action(action)
                            .payload(payloadJson)
                            .previousHash(previousHash)
                            .currentHash(currentHash)
                            .blockIndex(blockIndex)
                            .build();

            historyRepository.save(block);

            log.info(
                    "Created block {} for task {}",
                    blockIndex,
                    task.getId()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to create blockchain entry",
                    ex
            );

            throw new RuntimeException(
                    "Failed to create blockchain entry",
                    ex
            );
        }
    }
    /**
     * Maps a Task to a safe, serializable snapshot.
     * Only primitive/value fields — no JPA entities, no sensitive data.
     */
    private TaskHistoryPayload buildPayload(Task task) {
        return TaskHistoryPayload.builder()
                .taskId(task.getId())
                .userId(task.getUser() != null ? task.getUser().getId() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority() != null
                        ? task.getPriority().name() : null)
                .status(task.getStatus() != null
                        ? task.getStatus().name() : null)
                .dueDate(task.getDueDate())
                .estimatedEffort(task.getEstimatedEffort())
                .deleted(task.getDeleted())
                .taskCreatedAt(task.getCreatedAt())
                .taskUpdatedAt(task.getUpdatedAt())
                .build();
    }
}