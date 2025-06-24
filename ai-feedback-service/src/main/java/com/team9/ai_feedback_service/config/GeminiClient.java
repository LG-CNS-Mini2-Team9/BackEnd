package com.team9.ai_feedback_service.config;

import com.google.genai.Client;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GeminiClient {

    @Value("${gemini.api.model:gemini-2.5-flash-001}")
    private String model;

    public final Client client;

    public GeminiClient(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
    }
}
