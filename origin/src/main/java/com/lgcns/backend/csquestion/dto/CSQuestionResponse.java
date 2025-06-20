package com.lgcns.backend.csquestion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lgcns.backend.global.domain.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSQuestionResponse {

    private Long id;
    private Category category;
    private LocalDateTime createdAt;
    private String content;
    private boolean isSubmitted;


    @Data
    @Builder
    public static class AIQuestionResponse {
        private Category category;
        private LocalDateTime createdAt;
        private String content;
        private String keyword;
    }
}
