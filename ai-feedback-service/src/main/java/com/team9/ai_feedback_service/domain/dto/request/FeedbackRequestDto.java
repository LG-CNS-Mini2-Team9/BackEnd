package com.team9.ai_feedback_service.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequestDto {
    private Long questionId;
    private Long answerId;
    private  String answer;

}
