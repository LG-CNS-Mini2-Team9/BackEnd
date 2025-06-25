package com.team9.ai_feedback_service.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AIFeedbackResponseDto {
    private int score;
    private String feedback;
}
