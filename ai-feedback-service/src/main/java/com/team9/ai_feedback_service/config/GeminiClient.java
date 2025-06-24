package com.team9.ai_feedback_service.config;

import com.google.genai.Client;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class GeminiClient {

    @Value("${GEMINI_MODEL:gemini-2.0-flash}")
    private String model;

    public final Client client;

    public GeminiClient(@Value("${GEMINI_API_KEY}") String apiKey) {
        log.info("제미나이 모델: {}", model);
        this.client = Client.builder().apiKey(apiKey).build();
    }
}
