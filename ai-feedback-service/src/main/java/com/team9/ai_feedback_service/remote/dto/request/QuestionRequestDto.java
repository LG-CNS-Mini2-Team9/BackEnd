package com.team9.ai_feedback_service.remote.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class QuestionRequestDto {

    private String category;
    private List<String> existingKeywords;
}
