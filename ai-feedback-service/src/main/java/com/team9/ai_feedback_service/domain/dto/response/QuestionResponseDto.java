package com.team9.ai_feedback_service.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {

    private String category;

    private String content;

    private String keyword;

    private String hint;
}
