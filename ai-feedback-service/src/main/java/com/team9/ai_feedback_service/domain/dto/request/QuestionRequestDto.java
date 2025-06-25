package com.team9.ai_feedback_service.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequestDto {

    private String category;
    private List<String> existingKeywords;
}
