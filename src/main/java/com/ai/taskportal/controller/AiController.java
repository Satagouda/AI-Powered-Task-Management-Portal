package com.ai.taskportal.controller;


import com.ai.taskportal.dto.AiGenerateRequest;
import com.ai.taskportal.dto.AiGenerateResponse;
import com.ai.taskportal.dto.ApiResponse;
import com.ai.taskportal.services.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI-powered task generation APIs")
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate")
    @Operation(summary = "Generate task details using AI")
    public ApiResponse<AiGenerateResponse> generate(
            @Valid
            @RequestBody
            AiGenerateRequest request
    ) {

        return ApiResponse.<AiGenerateResponse>builder()
                .success(true)
                .message("AI task details generated successfully")
                .data(
                        aiService.generateTaskDetails(
                                request.getTitle()
                        )
                )
                .build();
    }
}