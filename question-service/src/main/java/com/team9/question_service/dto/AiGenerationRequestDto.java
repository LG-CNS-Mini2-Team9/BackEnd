package com.team9.question_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// ai-feedback-service로 보낼 요청 형식
@Getter
@AllArgsConstructor
public class AiGenerationRequestDto {
    private String category;
    private List<String> existingKeywords;
}
