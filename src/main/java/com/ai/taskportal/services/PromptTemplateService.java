package com.ai.taskportal.services;


import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    public String buildTaskPrompt(
            String title
    ) {

        return """
                You are an expert project manager.

                Analyze the task title below.

                TASK:
                "%s"

                Return ONLY valid JSON.
                
                Rules:
                        - priority must be LOW, MEDIUM, or HIGH
                        - estimatedEffort must be an integer between 1 and 10
                        - 1 = very small task (few minutes)
                        - 5 = medium task (few hours)
                        - 10 = very complex task (multiple days)
                
                Response format:
                
                {
                  "description":"string",
                  "priority":"LOW|MEDIUM|HIGH",
                  "estimatedEffort":integer
                }

                No markdown.
                No explanations.
                No extra text.
                """
                .formatted(title);
    }
}
