package com.team9.statistic_service.service;

import com.team9.statistic_service.domain.dto.StatisticRequest;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import com.team9.statistic_service.domain.entity.UserStatisticEntity;
import com.team9.statistic_service.domain.repository.UserStatisticRepository;
import com.team9.statistic_service.global.exception.CustomException;
import com.team9.statistic_service.global.code.StatisticErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional

public class StatisticService {


    private final UserStatisticRepository userStatisticRepository;
    private final TierService tierService;
    private final GrassService grassService;
    private final GoalService goalService;


    //사용자 통계 요약 조회
    @Transactional(readOnly = true)
    public StatisticResponse.StatisticSummaryResponse getStatisticSummary(Long userId) {
        UserStatisticEntity userStatistic = getUserStatisticOrCreate(userId);

        Map<String, StatisticResponse.CategoryStatResponse> categoryStats = buildCategoryStats(userStatistic);

        return StatisticResponse.StatisticSummaryResponse.builder()
                .userId(userId)
                .totalAnswerCount(userStatistic.getTotalAnswerCount())
                .totalScore(userStatistic.getTotalScore())
                .averageScore(userStatistic.getAverageScore())
                .currentTier(userStatistic.getCurrentTier())
                .categoryStats(categoryStats)
                .lastUpdated(userStatistic.getUpdatedAt())
                .build();
    }

    //답변 저장 시 통계 업데이트
    public StatisticResponse.StatisticUpdateResponse updateAnswerStatistic(
            StatisticRequest.AnswerUpdateRequest request) {

        UserStatisticEntity userStatistic = getUserStatisticOrCreate(request.getUserId());
        String oldTier = userStatistic.getCurrentTier();

        // 통계 업데이트
        updateStatisticForAnswer(userStatistic, request.getCategory(), request.getScore());

        // 잔디 업데이트
        grassService.updateActivityRecord(request.getUserId(), request.getAnsweredAt().toLocalDate());

        // 티어 체크 및 업데이트
        String newTier = tierService.checkAndUpdateTier(userStatistic);
        boolean tierChanged = !oldTier.equals(newTier);

        // 목표 진행도 업데이트
        boolean goalAchieved = goalService.updateGoalProgress(
                request.getUserId(),
                request.getAnsweredAt().toLocalDate()
        );

        userStatisticRepository.save(userStatistic);

        return StatisticResponse.StatisticUpdateResponse.builder()
                .success(true)
                .message("통계가 업데이트되었습니다.")
                .tierChanged(tierChanged ? newTier : null)
                .goalAchieved(goalAchieved)
                .build();
    }

    //점수 업데이트 (AI 피드백 후) - oldScore 없이 처리
    public StatisticResponse.StatisticUpdateResponse updateScoreStatistic(
            StatisticRequest.ScoreUpdateRequest request) {

        UserStatisticEntity userStatistic = getUserStatisticOrCreate(request.getUserId());
        String oldTier = userStatistic.getCurrentTier();

        // TODO: answer-service에서 기존 점수를 조회해서 차감하는 로직 필요
        // 현재는 새 점수만 추가하는 방식으로 구현
        log.warn("점수 업데이트 시 기존 점수 정보가 필요합니다. answerId: {}", request.getAnswerId());

        // 새 점수만 반영 (임시 구현)
        updateCategoryStatistic(userStatistic, request.getCategory(), request.getNewScore(), 0L);
        recalculateAverageScores(userStatistic);

        // 티어 재계산
        String newTier = tierService.checkAndUpdateTier(userStatistic);
        boolean tierChanged = !oldTier.equals(newTier);

        userStatisticRepository.save(userStatistic);

        return StatisticResponse.StatisticUpdateResponse.builder()
                .success(true)
                .message("점수가 업데이트되었습니다.")
                .tierChanged(tierChanged ? newTier : null)
                .goalAchieved(false)
                .build();
    }

    //사용자 통계 가져오기 또는 생성 (public 메서드)
    public UserStatisticEntity getUserStatisticOrCreate(Long userId) {
        return userStatisticRepository.findByUserId(userId)
                .orElseGet(() -> createUserStatistic(userId));
    }

    //Private 메서드들


    //새 사용자 통계 생성
    private UserStatisticEntity createUserStatistic(Long userId) {
        UserStatisticEntity userStatistic = UserStatisticEntity.builder()
                .userId(userId)
                .totalAnswerCount(0L)
                // 11개 카테고리 답변 수 초기화
                .dataStructureCount(0L)
                .computerArchitectureCount(0L)
                .operatingSystemCount(0L)
                .databaseCount(0L)
                .networkCount(0L)
                .softwareEngineeringCount(0L)
                .algorithmCount(0L)
                .designPatternCount(0L)
                .webFrontendCount(0L)
                .webBackendCount(0L)
                .cloudCount(0L)
                // 11개 카테고리 점수 합계 초기화
                .totalScore(BigDecimal.ZERO)
                .dataStructureScore(BigDecimal.ZERO)
                .computerArchitectureScore(BigDecimal.ZERO)
                .operatingSystemScore(BigDecimal.ZERO)
                .databaseScore(BigDecimal.ZERO)
                .networkScore(BigDecimal.ZERO)
                .softwareEngineeringScore(BigDecimal.ZERO)
                .algorithmScore(BigDecimal.ZERO)
                .designPatternScore(BigDecimal.ZERO)
                .webFrontendScore(BigDecimal.ZERO)
                .webBackendScore(BigDecimal.ZERO)
                .cloudScore(BigDecimal.ZERO)
                // 11개 카테고리 평균 점수 초기화
                .averageScore(BigDecimal.ZERO)
                .dataStructureAvg(BigDecimal.ZERO)
                .computerArchitectureAvg(BigDecimal.ZERO)
                .operatingSystemAvg(BigDecimal.ZERO)
                .databaseAvg(BigDecimal.ZERO)
                .networkAvg(BigDecimal.ZERO)
                .softwareEngineeringAvg(BigDecimal.ZERO)
                .algorithmAvg(BigDecimal.ZERO)
                .designPatternAvg(BigDecimal.ZERO)
                .webFrontendAvg(BigDecimal.ZERO)
                .webBackendAvg(BigDecimal.ZERO)
                .cloudAvg(BigDecimal.ZERO)
                .currentTier("뉴비")
                .build();

        return userStatisticRepository.save(userStatistic);
    }


    //카테고리별 통계 응답 생성
    private Map<String, StatisticResponse.CategoryStatResponse> buildCategoryStats(UserStatisticEntity userStatistic) {
        Map<String, StatisticResponse.CategoryStatResponse> categoryStats = new HashMap<>();

        categoryStats.put("자료구조", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getDataStructureCount())
                .score(userStatistic.getDataStructureScore())
                .average(userStatistic.getDataStructureAvg())
                .build());
        categoryStats.put("컴퓨터구조", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getComputerArchitectureCount())
                .score(userStatistic.getComputerArchitectureScore())
                .average(userStatistic.getComputerArchitectureAvg())
                .build());
        categoryStats.put("운영체제", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getOperatingSystemCount())
                .score(userStatistic.getOperatingSystemScore())
                .average(userStatistic.getOperatingSystemAvg())
                .build());
        categoryStats.put("데이터베이스", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getDatabaseCount())
                .score(userStatistic.getDatabaseScore())
                .average(userStatistic.getDatabaseAvg())
                .build());
        categoryStats.put("네트워크", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getNetworkCount())
                .score(userStatistic.getNetworkScore())
                .average(userStatistic.getNetworkAvg())
                .build());
        categoryStats.put("소프트웨어공학", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getSoftwareEngineeringCount())
                .score(userStatistic.getSoftwareEngineeringScore())
                .average(userStatistic.getSoftwareEngineeringAvg())
                .build());
        categoryStats.put("알고리즘", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getAlgorithmCount())
                .score(userStatistic.getAlgorithmScore())
                .average(userStatistic.getAlgorithmAvg())
                .build());
        categoryStats.put("디자인패턴", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getDesignPatternCount())
                .score(userStatistic.getDesignPatternScore())
                .average(userStatistic.getDesignPatternAvg())
                .build());
        categoryStats.put("웹프론트엔드", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getWebFrontendCount())
                .score(userStatistic.getWebFrontendScore())
                .average(userStatistic.getWebFrontendAvg())
                .build());
        categoryStats.put("웹백엔드", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getWebBackendCount())
                .score(userStatistic.getWebBackendScore())
                .average(userStatistic.getWebBackendAvg())
                .build());
        categoryStats.put("클라우드", StatisticResponse.CategoryStatResponse.builder()
                .count(userStatistic.getCloudCount())
                .score(userStatistic.getCloudScore())
                .average(userStatistic.getCloudAvg())
                .build());

        return categoryStats;
    }


    //답변에 대한 통계 업데이트
    private void updateStatisticForAnswer(UserStatisticEntity userStatistic, String category, BigDecimal score) {
        // 총 답변 수 및 점수 증가
        userStatistic.setTotalAnswerCount(userStatistic.getTotalAnswerCount() + 1);
        userStatistic.setTotalScore(userStatistic.getTotalScore().add(score));

        // 카테고리별 업데이트
        updateCategoryStatistic(userStatistic, category, score, 1L);

        // 평균 점수 재계산
        recalculateAverageScores(userStatistic);
    }


    //카테고리별 통계 업데이트
    private void updateCategoryStatistic(UserStatisticEntity userStatistic, String category, BigDecimal score, Long countIncrement) {
        switch (category) {
            case "자료구조":
                userStatistic.setDataStructureCount(userStatistic.getDataStructureCount() + countIncrement);
                userStatistic.setDataStructureScore(userStatistic.getDataStructureScore().add(score));
                break;
            case "컴퓨터구조":
                userStatistic.setComputerArchitectureCount(userStatistic.getComputerArchitectureCount() + countIncrement);
                userStatistic.setComputerArchitectureScore(userStatistic.getComputerArchitectureScore().add(score));
                break;
            case "운영체제":
                userStatistic.setOperatingSystemCount(userStatistic.getOperatingSystemCount() + countIncrement);
                userStatistic.setOperatingSystemScore(userStatistic.getOperatingSystemScore().add(score));
                break;
            case "데이터베이스":
                userStatistic.setDatabaseCount(userStatistic.getDatabaseCount() + countIncrement);
                userStatistic.setDatabaseScore(userStatistic.getDatabaseScore().add(score));
                break;
            case "네트워크":
                userStatistic.setNetworkCount(userStatistic.getNetworkCount() + countIncrement);
                userStatistic.setNetworkScore(userStatistic.getNetworkScore().add(score));
                break;
            case "소프트웨어공학":
                userStatistic.setSoftwareEngineeringCount(userStatistic.getSoftwareEngineeringCount() + countIncrement);
                userStatistic.setSoftwareEngineeringScore(userStatistic.getSoftwareEngineeringScore().add(score));
                break;
            case "알고리즘":
                userStatistic.setAlgorithmCount(userStatistic.getAlgorithmCount() + countIncrement);
                userStatistic.setAlgorithmScore(userStatistic.getAlgorithmScore().add(score));
                break;
            case "디자인패턴":
                userStatistic.setDesignPatternCount(userStatistic.getDesignPatternCount() + countIncrement);
                userStatistic.setDesignPatternScore(userStatistic.getDesignPatternScore().add(score));
                break;
            case "웹프론트엔드":
                userStatistic.setWebFrontendCount(userStatistic.getWebFrontendCount() + countIncrement);
                userStatistic.setWebFrontendScore(userStatistic.getWebFrontendScore().add(score));
                break;
            case "웹백엔드":
                userStatistic.setWebBackendCount(userStatistic.getWebBackendCount() + countIncrement);
                userStatistic.setWebBackendScore(userStatistic.getWebBackendScore().add(score));
                break;
            case "클라우드":
                userStatistic.setCloudCount(userStatistic.getCloudCount() + countIncrement);
                userStatistic.setCloudScore(userStatistic.getCloudScore().add(score));
                break;
            default:
                throw new CustomException(StatisticErrorCode.INVALID_CATEGORY);
        }
    }


    //평균 점수 재계산
    private void recalculateAverageScores(UserStatisticEntity userStatistic) {
        // 전체 평균
        if (userStatistic.getTotalAnswerCount() > 0) {
            userStatistic.setAverageScore(
                    userStatistic.getTotalScore()
                            .divide(BigDecimal.valueOf(userStatistic.getTotalAnswerCount()), 2, RoundingMode.HALF_UP)
            );
        }

        // 11개 카테고리별 평균
        updateCategoryAverage(userStatistic, "자료구조");
        updateCategoryAverage(userStatistic, "컴퓨터구조");
        updateCategoryAverage(userStatistic, "운영체제");
        updateCategoryAverage(userStatistic, "데이터베이스");
        updateCategoryAverage(userStatistic, "네트워크");
        updateCategoryAverage(userStatistic, "소프트웨어공학");
        updateCategoryAverage(userStatistic, "알고리즘");
        updateCategoryAverage(userStatistic, "디자인패턴");
        updateCategoryAverage(userStatistic, "웹프론트엔드");
        updateCategoryAverage(userStatistic, "웹백엔드");
        updateCategoryAverage(userStatistic, "클라우드");
    }

    //카테고리별 평균 계산
    private void updateCategoryAverage(UserStatisticEntity userStatistic, String category) {
        Long count;
        BigDecimal score;

        switch (category) {
            case "자료구조":
                count = userStatistic.getDataStructureCount();
                score = userStatistic.getDataStructureScore();
                if (count > 0) {
                    userStatistic.setDataStructureAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "컴퓨터구조":
                count = userStatistic.getComputerArchitectureCount();
                score = userStatistic.getComputerArchitectureScore();
                if (count > 0) {
                    userStatistic.setComputerArchitectureAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "운영체제":
                count = userStatistic.getOperatingSystemCount();
                score = userStatistic.getOperatingSystemScore();
                if (count > 0) {
                    userStatistic.setOperatingSystemAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "데이터베이스":
                count = userStatistic.getDatabaseCount();
                score = userStatistic.getDatabaseScore();
                if (count > 0) {
                    userStatistic.setDatabaseAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "네트워크":
                count = userStatistic.getNetworkCount();
                score = userStatistic.getNetworkScore();
                if (count > 0) {
                    userStatistic.setNetworkAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "소프트웨어공학":
                count = userStatistic.getSoftwareEngineeringCount();
                score = userStatistic.getSoftwareEngineeringScore();
                if (count > 0) {
                    userStatistic.setSoftwareEngineeringAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "알고리즘":
                count = userStatistic.getAlgorithmCount();
                score = userStatistic.getAlgorithmScore();
                if (count > 0) {
                    userStatistic.setAlgorithmAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "디자인패턴":
                count = userStatistic.getDesignPatternCount();
                score = userStatistic.getDesignPatternScore();
                if (count > 0) {
                    userStatistic.setDesignPatternAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "웹프론트엔드":
                count = userStatistic.getWebFrontendCount();
                score = userStatistic.getWebFrontendScore();
                if (count > 0) {
                    userStatistic.setWebFrontendAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "웹백엔드":
                count = userStatistic.getWebBackendCount();
                score = userStatistic.getWebBackendScore();
                if (count > 0) {
                    userStatistic.setWebBackendAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
            case "클라우드":
                count = userStatistic.getCloudCount();
                score = userStatistic.getCloudScore();
                if (count > 0) {
                    userStatistic.setCloudAvg(score.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                }
                break;
        }
    }
}