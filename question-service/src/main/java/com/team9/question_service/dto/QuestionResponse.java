package com.team9.question_service.dto;

import com.team9.common.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private Category category;
    private LocalDateTime createdAt;
    private String content;
    private String difficulty;
    private Map<String, Object> hint;
    private boolean isSubmitted;
}