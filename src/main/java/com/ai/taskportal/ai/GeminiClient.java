package com.ai.taskportal.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient webClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public String generateContent(
            String prompt
    ) {

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """
                .formatted(
                        prompt.replace("\"", "\\\"")
                );

        return webClient.post()
                .uri(
                        "https://generativelanguage.googleapis.com/v1beta/models/"
                                + model
                                + ":generateContent?key="
                                + apiKey
                )
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
