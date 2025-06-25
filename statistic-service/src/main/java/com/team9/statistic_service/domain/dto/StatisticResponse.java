package com.team9.statistic_service.domain.dto;

import com.team9.statistic_service.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticResponse {

    // 사용자 기본 정보
    private String userName;                          // 사용자 이름

    // 티어 정보
    private TierInfo tierInfo;                        // 티어 표시 및 달성도

    // 잔디 정보
    private GrassData grassData;                      // 잔디 데이터

    // 전체 통계
    private double totalAverageScore;                 // 총 평균 점수
    private long totalAnswerCount;                    // 총 답변 수

    // 카테고리별 통계
    private Map<Category, CategoryStat> categoryStats; // 카테고리별 평균 점수와 답변 수

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStat {
        private double averageScore;   // 카테고리별 평균 점수
        private long answerCount;      // 카테고리별 답변 수
    }
}