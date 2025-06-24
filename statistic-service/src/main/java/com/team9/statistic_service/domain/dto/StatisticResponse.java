package com.team9.statistic_service.domain.dto;

import com.team9.statistic_service.domain.entity.GoalEntity.GoalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StatisticResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticSummaryResponse {
        private Long userId;
        private Long totalAnswerCount;
        private BigDecimal totalScore;
        private BigDecimal averageScore;
        private String currentTier;
        private Map<String, CategoryStatResponse> categoryStats;
        private LocalDateTime lastUpdated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryStatResponse {
        private Long count;
        private BigDecimal score;
        private BigDecimal average;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GrassResponse {
        private Integer year;
        private Long totalActiveDays;
        private Integer currentStreak;
        private Integer longestStreak;
        private List<ActivityResponse> activities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivityResponse {
        private LocalDate date;
        private Integer answerCount;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TierResponse {
        private String currentTier;
        private Integer tierLevel;
        private TierProgressResponse progress;
        private TierInfoResponse nextTier;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TierProgressResponse {
        private ProgressItemResponse answerCount;
        private ProgressItemResponse categoryAnswers;
        private ProgressItemResponse averageScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressItemResponse {
        private Number current;
        private Number required;
        private Boolean completed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TierInfoResponse {
        private String tierName;
        private Integer tierLevel;
        private TierRequirementsResponse requirements;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TierRequirementsResponse {
        private Integer answerCount;
        private Integer categoryAnswers;
        private BigDecimal averageScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoalResponse {
        private Long id;
        private GoalType goalType;
        private Integer targetCount;
        private Integer currentCount;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isAchieved;
        private Boolean isActive;
        private Double progressPercentage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticUpdateResponse {
        private Boolean success;
        private String message;
        private String tierChanged;
        private Boolean goalAchieved;
    }
}
