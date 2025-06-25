package com.team9.answer_service.remote.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequestDto {
    private Long questionId;
    private Long answerId;
    private String answer;

}
