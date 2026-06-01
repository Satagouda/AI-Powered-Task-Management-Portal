package com.ai.taskportal.ai;

import com.ai.taskportal.dto.AiGenerateResponse;
import com.ai.taskportal.entity.TaskPriority;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiResponseParser {

    private final ObjectMapper objectMapper;

    public AiGenerateResponse parse(
            String response
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(response);

            String text =
                    root.path("candidates")
                            .get(0)
                            .path("content")
                            .path("parts")
                            .get(0)
                            .path("text")
                            .asText();

            text = text
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode aiJson =
                    objectMapper.readTree(text);

            return AiGenerateResponse.builder()
                    .description(
                            aiJson.get("description")
                                    .asText()
                    )
                    .priority(
                            TaskPriority.valueOf(
                                    aiJson.get("priority")
                                            .asText()
                            )
                    )
                    .estimatedEffort(
                            aiJson.get("estimatedEffort")
                                    .asInt()
                    )
                    .build();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to parse Gemini response",
                    ex
            );
        }
    }
}