package com.team9.ai_feedback_service.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackScoreResponseDto {
    private Long answerId;
    private int score;

    public static FeedbackScoreResponseDto of(Long answerId, int score) {
        return new FeedbackScoreResponseDto(answerId, score);
    }
}
