package com.team9.question_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ai-feedback-service로부터 받을 응답 형식
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCreatedQuestionDto {
    private String category;
    private String content;
    private String keyword;
    private String hint;
}
