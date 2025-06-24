package com.team9.statistic_service.remote.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RemoteServiceResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDto {
        private Long id;
        private String email;
        private String nickname;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerDto {
        private Long id;
        private Long userId;
        private Long questionId;
        private String category;
        private String content;
        private BigDecimal score;
        private String feedback;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionDto {
        private Long id;
        private String category;
        private String content;
        private String difficulty;
        private LocalDateTime createdAt;
    }
}
