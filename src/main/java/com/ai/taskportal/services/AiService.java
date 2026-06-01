package com.ai.taskportal.services;

import com.ai.taskportal.ai.GeminiClient;
import com.ai.taskportal.ai.GeminiResponseParser;
import com.ai.taskportal.dto.AiGenerateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final GeminiClient geminiClient;
    private final PromptTemplateService promptTemplateService;
    private final GeminiResponseParser parser;

    public AiGenerateResponse generateTaskDetails(
            String title
    ) {

        String prompt =
                promptTemplateService
                        .buildTaskPrompt(title);

        String rawResponse =
                geminiClient.generateContent(prompt);

        return parser.parse(rawResponse);
    }
}
