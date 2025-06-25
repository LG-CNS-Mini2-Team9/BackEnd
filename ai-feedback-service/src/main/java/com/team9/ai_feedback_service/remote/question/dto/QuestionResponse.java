package com.team9.ai_feedback_service.remote.question.dto;

import com.team9.common.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private Category category;
    private LocalDateTime createdAt;
    private String content;
    private String hint;
    private boolean isSubmitted;

}