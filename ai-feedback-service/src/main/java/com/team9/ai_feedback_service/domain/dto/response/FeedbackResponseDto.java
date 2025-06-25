package com.team9.ai_feedback_service.domain.dto.response;

import com.team9.ai_feedback_service.domain.AIFeedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponseDto {
    private Long answerId;
    private String feedback;
    private int score;

    public static FeedbackResponseDto of(AIFeedback aiFeedback) {
        return new FeedbackResponseDto(aiFeedback.getAnswerId(), aiFeedback.getFeedback(), aiFeedback.getScore());
    }
}
