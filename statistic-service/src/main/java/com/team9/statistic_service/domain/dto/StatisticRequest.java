package com.team9.statistic_service.domain.dto;

import com.team9.statistic_service.domain.entity.GoalEntity.GoalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class StatisticRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoalCreateRequest {
        @NotNull(message = "목표 타입은 필수입니다")
        private GoalType goalType;

        @NotNull(message = "목표 수량은 필수입니다")
        @Positive(message = "목표 수량은 0보다 커야 합니다")
        private Integer targetCount;

        @NotNull(message = "시작일은 필수입니다")
        private LocalDate startDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoalUpdateRequest {
        @Positive(message = "목표 수량은 0보다 커야 합니다")
        private Integer targetCount;

        private LocalDate endDate;

        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerUpdateRequest {
        @NotNull(message = "사용자 ID는 필수입니다")
        private Long userId;

        @NotBlank(message = "카테고리는 필수입니다")
        private String category;

        @NotNull(message = "점수는 필수입니다")
        private BigDecimal score;

        @NotNull(message = "답변 시간은 필수입니다")
        private LocalDateTime answeredAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreUpdateRequest {
        @NotNull(message = "사용자 ID는 필수입니다")
        private Long userId;

        @NotNull(message = "답변 ID는 필수입니다")
        private Long answerId;

        @NotBlank(message = "카테고리는 필수입니다")
        private String category;

        @NotNull(message = "새로운 점수는 필수입니다")
        private BigDecimal newScore;
    }
}
