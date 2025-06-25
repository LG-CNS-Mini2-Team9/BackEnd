package com.team9.statistic_service.service;

import com.team9.statistic_service.domain.enums.Category;
import com.team9.statistic_service.domain.dto.TierInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class TierService {

    /**
     * 티어 조건:
     * 1단계(뉴비) - 초기값
     * 2단계(루키) - 답변 수 5개 이상
     * 3단계(에이스) - 답변 수 15개 이상 + 카테고리별 1개 이상 답변
     * 4단계(마스터) - 답변 수 40개 이상 + 카테고리별 3개 이상 답변 + 총 답변 평균 점수 70점 이상
     * 5단계(레전드) - 답변 수 100개 이상 + 카테고리별 5개 이상 답변 + 총 답변 평균 점수 85점 이상
     */

    public TierInfo calculateTierInfo(long totalAnswerCount, double totalAverageScore, Map<Category, Long> categoryAnswerCounts) {
        String currentTier = determineTier(totalAnswerCount, totalAverageScore, categoryAnswerCounts);
        String nextTier = getNextTier(currentTier);
        double progressPercent = calculateProgress(totalAnswerCount, totalAverageScore, categoryAnswerCounts, currentTier);
        String progressMessage = createProgressMessage(totalAnswerCount, totalAverageScore, categoryAnswerCounts, currentTier);

        return TierInfo.builder()
                .currentTier(currentTier)
                .nextTier(nextTier)
                .progressPercent(progressPercent)
                .progressMessage(progressMessage)
                .build();
    }

    private String determineTier(long totalAnswerCount, double totalAverageScore, Map<Category, Long> categoryAnswerCounts) {
        // 5단계 (레전드) 체크
        if (totalAnswerCount >= 100 && totalAverageScore >= 85 && allCategoriesHaveMinAnswers(categoryAnswerCounts, 5)) {
            return "레전드";
        }

        // 4단계 (마스터) 체크
        if (totalAnswerCount >= 40 && totalAverageScore >= 70 && allCategoriesHaveMinAnswers(categoryAnswerCounts, 3)) {
            return "마스터";
        }

        // 3단계 (에이스) 체크
        if (totalAnswerCount >= 15 && allCategoriesHaveMinAnswers(categoryAnswerCounts, 1)) {
            return "에이스";
        }

        // 2단계 (루키) 체크
        if (totalAnswerCount >= 5) {
            return "루키";
        }

        // 1단계 (뉴비) - 기본값
        return "뉴비";
    }

    private boolean allCategoriesHaveMinAnswers(Map<Category, Long> categoryAnswerCounts, int minAnswers) {
        // 모든 카테고리에 최소 답변 수가 있는지 확인
        for (Category category : Category.values()) {
            long count = categoryAnswerCounts.getOrDefault(category, 0L);
            if (count < minAnswers) {
                return false;
            }
        }
        return true;
    }

    private String getNextTier(String currentTier) {
        switch (currentTier) {
            case "뉴비": return "루키";
            case "루키": return "에이스";
            case "에이스": return "마스터";
            case "마스터": return "레전드";
            case "레전드": return null; // 최고 티어
            default: return "루키";
        }
    }

    private double calculateProgress(long totalAnswerCount, double totalAverageScore, Map<Category, Long> categoryAnswerCounts, String currentTier) {
        switch (currentTier) {
            case "뉴비":
                // 루키까지 필요한 답변 수: 5개
                return Math.min(100.0, (totalAnswerCount / 5.0) * 100);

            case "루키":
                // 에이스까지 필요한 조건: 답변 수 15개 + 모든 카테고리 1개 이상
                double answerProgress = Math.min(100.0, (totalAnswerCount / 15.0) * 50); // 50% 가중치
                double categoryProgress = calculateCategoryProgress(categoryAnswerCounts, 1) * 50; // 50% 가중치
                return answerProgress + categoryProgress;

            case "에이스":
                // 마스터까지 필요한 조건: 답변 수 40개 + 모든 카테고리 3개 이상 + 평균 70점
                double answerProg = Math.min(100.0, (totalAnswerCount / 40.0) * 40); // 40% 가중치
                double categoryProg = calculateCategoryProgress(categoryAnswerCounts, 3) * 30; // 30% 가중치
                double scoreProg = Math.min(100.0, (totalAverageScore / 70.0) * 30); // 30% 가중치
                return answerProg + categoryProg + scoreProg;

            case "마스터":
                // 레전드까지 필요한 조건: 답변 수 100개 + 모든 카테고리 5개 이상 + 평균 85점
                double answerProgMaster = Math.min(100.0, (totalAnswerCount / 100.0) * 40);
                double categoryProgMaster = calculateCategoryProgress(categoryAnswerCounts, 5) * 30;
                double scoreProgMaster = Math.min(100.0, (totalAverageScore / 85.0) * 30);
                return answerProgMaster + categoryProgMaster + scoreProgMaster;

            case "레전드":
                return 100.0; // 최고 티어

            default:
                return 0.0;
        }
    }

    private double calculateCategoryProgress(Map<Category, Long> categoryAnswerCounts, int targetMinAnswers) {
        int totalCategories = Category.values().length;
        int satisfiedCategories = 0;

        for (Category category : Category.values()) {
            long count = categoryAnswerCounts.getOrDefault(category, 0L);
            if (count >= targetMinAnswers) {
                satisfiedCategories++;
            }
        }

        return (double) satisfiedCategories / totalCategories * 100;
    }

    private String createProgressMessage(long totalAnswerCount, double totalAverageScore, Map<Category, Long> categoryAnswerCounts, String currentTier) {
        switch (currentTier) {
            case "뉴비":
                long neededAnswers = 5 - totalAnswerCount;
                return neededAnswers > 0 ? String.format("루키까지 %d개의 답변이 더 필요해요!", neededAnswers) : "루키 달성 조건을 만족했어요!";

            case "루키":
                StringBuilder message = new StringBuilder();
                if (totalAnswerCount < 15) {
                    message.append(String.format("답변 %d개 더 필요 ", 15 - totalAnswerCount));
                }

                int unsatisfiedCategories = 0;
                for (Category category : Category.values()) {
                    if (categoryAnswerCounts.getOrDefault(category, 0L) < 1) {
                        unsatisfiedCategories++;
                    }
                }

                if (unsatisfiedCategories > 0) {
                    message.append(String.format("(카테고리 %d개 더 도전 필요)", unsatisfiedCategories));
                }

                return message.length() > 0 ? message.toString() : "에이스 달성 조건을 만족했어요!";

            case "에이스":
                StringBuilder msgMaster = new StringBuilder();
                if (totalAnswerCount < 40) {
                    msgMaster.append(String.format("답변 %d개 더 필요 ", 40 - totalAnswerCount));
                }
                if (totalAverageScore < 70) {
                    msgMaster.append(String.format("평균점수 %.1f점 더 필요 ", 70 - totalAverageScore));
                }

                int unsatisfiedCats = 0;
                for (Category category : Category.values()) {
                    if (categoryAnswerCounts.getOrDefault(category, 0L) < 3) {
                        unsatisfiedCats++;
                    }
                }

                if (unsatisfiedCats > 0) {
                    msgMaster.append(String.format("(카테고리별 3개 이상 답변 %d개 카테고리 더 필요)", unsatisfiedCats));
                }

                return msgMaster.length() > 0 ? msgMaster.toString() : "마스터 달성 조건을 만족했어요!";

            case "마스터":
                StringBuilder msgLegend = new StringBuilder();
                if (totalAnswerCount < 100) {
                    msgLegend.append(String.format("답변 %d개 더 필요 ", 100 - totalAnswerCount));
                }
                if (totalAverageScore < 85) {
                    msgLegend.append(String.format("평균점수 %.1f점 더 필요 ", 85 - totalAverageScore));
                }

                int unsatisfiedCatsLegend = 0;
                for (Category category : Category.values()) {
                    if (categoryAnswerCounts.getOrDefault(category, 0L) < 5) {
                        unsatisfiedCatsLegend++;
                    }
                }

                if (unsatisfiedCatsLegend > 0) {
                    msgLegend.append(String.format("(카테고리별 5개 이상 답변 %d개 카테고리 더 필요)", unsatisfiedCatsLegend));
                }

                return msgLegend.length() > 0 ? msgLegend.toString() : "레전드 달성 조건을 만족했어요!";

            case "레전드":
                return "최고 티어에 도달했습니다! 정말 대단해요! 🎉";

            default:
                return "";
        }
    }
}