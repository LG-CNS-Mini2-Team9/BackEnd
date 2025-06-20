package com.lgcns.backend.ai.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

public class AIRequest {

    @Data
    @Builder
    public static class OpenAiRequest {
        private String model;
        private List<Map<String, String>> messages;
        private double temperature;
        private int max_tokens;
    }
}
