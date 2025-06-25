package com.team9.answer_service.remote.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackScoreResponseDto {
    private Long answerId;
    private int score;
}
