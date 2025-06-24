package com.team9.ai_feedback_service.domain.dto.response;

import lombok.Getter;

@Getter
public class FeedbackScoreResponseDto {
    private Long answerId;
    private int score;

    public FeedbackScoreResponseDto(Long answerId, int score) {
        this.answerId = answerId;
        this.score = score;
    }
}
