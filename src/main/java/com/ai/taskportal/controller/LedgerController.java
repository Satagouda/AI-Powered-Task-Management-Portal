package com.ai.taskportal.controller;


import com.ai.taskportal.dto.ApiResponse;
import com.ai.taskportal.entity.TaskHistory;
import com.ai.taskportal.services.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(
        name = "Blockchain Ledger",
        description = "Blockchain audit trail APIs"
)
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/{taskId}")
    @Operation(
            summary = "Get blockchain history"
    )
    public ApiResponse<List<TaskHistory>> getLedger(
            @PathVariable UUID taskId
    ) {

        return ApiResponse.<List<TaskHistory>>builder()
                .success(true)
                .message("Ledger fetched successfully")
                .data(
                        ledgerService.getLedger(
                                taskId
                        )
                )
                .build();
    }

    @GetMapping("/verify/{taskId}")
    @Operation(
            summary = "Verify blockchain integrity"
    )
    public ApiResponse<Map<String, Object>>
    verifyLedger(
            @PathVariable UUID taskId
    ) {

        boolean valid =
                ledgerService.verifyChain(
                        taskId
                );

        return ApiResponse.<Map<String, Object>>
                        builder()
                .success(true)
                .message("Verification completed")
                .data(
                        Map.of(
                                "valid",
                                valid
                        )
                )
                .build();
    }
}
