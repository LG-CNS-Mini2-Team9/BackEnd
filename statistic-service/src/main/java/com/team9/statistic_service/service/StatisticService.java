package com.team9.statistic_service.service;

import com.team9.statistic_service.domain.enums.Category;
import com.team9.statistic_service.domain.dto.GrassData;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import com.team9.statistic_service.domain.dto.TierInfo;
import com.team9.statistic_service.remote.answer.RemoteAnswerService;
import com.team9.statistic_service.remote.answer.dto.CSStatisticResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {

    private final RemoteAnswerService remoteAnswerService;
    private final TierService tierService;

    /**
     * 사용자의 전체 통계 정보를 조회
     * answer-service에서 데이터를 받아와서 모든 통계를 계산
     */
    public StatisticResponse getUserStatistics(String authorization, String userName) {
        // answer-service에서 모든 답변 데이터 받아오기
        List<CSStatisticResponse> allAnswers = remoteAnswerService.getMyAnswers(authorization, null);

        if (allAnswers.isEmpty()) {
            return createEmptyStatistics(userName);
        }

        // 1. 전체 평균 점수 계산
        double totalAverageScore = allAnswers.stream()
                .mapToInt(CSStatisticResponse::getCsanswer_score)
                .average()
                .orElse(0.0);

        // 2. 총 답변 수
        long totalAnswerCount = allAnswers.size();

        // 3. 카테고리별 평균 점수 계산
        Map<Category, Double> categoryAverageScores = allAnswers.stream()
                .collect(Collectors.groupingBy(
                        CSStatisticResponse::getCsquestion_category,
                        Collectors.averagingInt(CSStatisticResponse::getCsanswer_score)
                ));

        // 4. 카테고리별 답변 수 계산
        Map<Category, Long> categoryAnswerCounts = allAnswers.stream()
                .collect(Collectors.groupingBy(
                        CSStatisticResponse::getCsquestion_category,
                        Collectors.counting()
                ));

        // 5. 카테고리별 통계 생성
        Map<Category, StatisticResponse.CategoryStat> categoryStats = new HashMap<>();
        for (Category category : Category.values()) {
            double avgScore = categoryAverageScores.getOrDefault(category, 0.0);
            long count = categoryAnswerCounts.getOrDefault(category, 0L);

            categoryStats.put(category, StatisticResponse.CategoryStat.builder()
                    .averageScore(avgScore)
                    .answerCount(count)
                    .build());
        }

        // 6. 티어 정보 계산
        TierInfo tierInfo = tierService.calculateTierInfo(totalAnswerCount, totalAverageScore, categoryAnswerCounts);

        // 7. 잔디 데이터 계산 (question_id 기준으로 날짜별 답변 여부 확인)
        GrassData grassData = calculateGrassData(allAnswers);

        return StatisticResponse.builder()
                .userName(userName)
                .tierInfo(tierInfo)
                .grassData(grassData)
                .totalAverageScore(Math.round(totalAverageScore * 100.0) / 100.0) // 소수점 2자리
                .totalAnswerCount(totalAnswerCount)
                .categoryStats(categoryStats)
                .build();
    }

    /**
     * 잔디 데이터 계산
     * 하루에 한 번 이상 답변하면 해당 날짜는 활동한 것으로 간주
     */
    private GrassData calculateGrassData(List<CSStatisticResponse> answers) {
        // question_id를 기준으로 답변한 날짜들을 추출
        // 실제로는 answer 엔티티에 createdAt이 있어야 하는데, 현재 DTO에는 없음
        // 임시로 현재 날짜 기준으로 최근 1년간의 더미 데이터 생성

        Map<String, Boolean> dailyActivity = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 최근 1년간 날짜 생성
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);

        // 답변한 질문 ID 개수 기준으로 활동 날짜 시뮬레이션
        Set<Long> answeredQuestionIds = answers.stream()
                .map(CSStatisticResponse::getCsquestion_id)
                .collect(Collectors.toSet());

        List<String> activeDates = new ArrayList<>();

        // 간단한 시뮬레이션: 답변한 질문 수만큼 최근 날짜부터 역순으로 활동했다고 가정
        int questionsAnswered = answeredQuestionIds.size();
        LocalDate currentDate = today;

        for (int i = 0; i < questionsAnswered && !currentDate.isBefore(oneYearAgo); i++) {
            String dateStr = currentDate.format(formatter);
            dailyActivity.put(dateStr, true);
            activeDates.add(dateStr);

            // 1-3일 간격으로 랜덤하게 배치 (더 현실적으로)
            int daysBack = 1 + (int) (Math.random() * 3);
            currentDate = currentDate.minusDays(daysBack);
        }

        // 나머지 날짜는 활동 없음으로 설정
        LocalDate dateIterator = oneYearAgo;
        while (!dateIterator.isAfter(today)) {
            String dateStr = dateIterator.format(formatter);
            dailyActivity.putIfAbsent(dateStr, false);
            dateIterator = dateIterator.plusDays(1);
        }

        // 최장 연속 학습일 계산
        int maxConsecutiveDays = calculateMaxConsecutiveDays(activeDates);

        // 현재 연속 학습일 계산
        int currentStreak = calculateCurrentStreak(dailyActivity, today);

        return GrassData.builder()
                .dailyActivity(dailyActivity)
                .maxConsecutiveDays(maxConsecutiveDays)
                .currentStreak(currentStreak)
                .activeDates(activeDates)
                .build();
    }

    /**
     * 최장 연속 학습일 계산
     */
    private int calculateMaxConsecutiveDays(List<String> activeDates) {
        if (activeDates.isEmpty()) {
            return 0;
        }

        // 날짜 정렬
        List<LocalDate> sortedDates = activeDates.stream()
                .map(LocalDate::parse)
                .sorted()
                .collect(Collectors.toList());

        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDate prevDate = sortedDates.get(i - 1);
            LocalDate currentDate = sortedDates.get(i);

            // 연속된 날짜인지 확인
            if (prevDate.plusDays(1).equals(currentDate)) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return maxStreak;
    }

    /**
     * 현재 연속 학습일 계산
     */
    private int calculateCurrentStreak(Map<String, Boolean> dailyActivity, LocalDate today) {
        int streak = 0;
        LocalDate currentDate = today;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 오늘부터 역순으로 연속된 활동 날짜 확인
        while (dailyActivity.getOrDefault(currentDate.format(formatter), false)) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }

        return streak;
    }

    /**
     * 데이터가 없는 경우 빈 통계 반환
     */
    private StatisticResponse createEmptyStatistics(String userName) {
        Map<Category, StatisticResponse.CategoryStat> categoryStats = new HashMap<>();

        // 모든 카테고리에 대해 빈 통계 생성
        for (Category category : Category.values()) {
            categoryStats.put(category, StatisticResponse.CategoryStat.builder()
                    .averageScore(0.0)
                    .answerCount(0L)
                    .build());
        }

        TierInfo tierInfo = TierInfo.builder()
                .currentTier("뉴비")
                .nextTier("루키")
                .progressPercent(0.0)
                .progressMessage("첫 번째 답변을 작성해보세요!")
                .build();

        GrassData grassData = GrassData.builder()
                .dailyActivity(new HashMap<>())
                .maxConsecutiveDays(0)
                .currentStreak(0)
                .activeDates(new ArrayList<>())
                .build();

        return StatisticResponse.builder()
                .userName(userName)
                .tierInfo(tierInfo)
                .grassData(grassData)
                .totalAverageScore(0.0)
                .totalAnswerCount(0L)
                .categoryStats(categoryStats)
                .build();
    }
}