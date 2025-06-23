package com.team9.statistic_service.service;

import com.team9.statistic_service.domain.entity.TierInfoEntity;
import com.team9.statistic_service.domain.entity.UserStatisticEntity;
import com.team9.statistic_service.domain.repository.TierInfoRepository;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TierService {

    private final TierInfoRepository tierInfoRepository;


//    티어 조건 체크 및 업데이트
//    티어 승급 조건을 만족하면 티어 업데이트
    public String checkAndUpdateTier(UserStatisticEntity userStatistic) {
        List<TierInfoEntity> allTiers = tierInfoRepository.findAllOrderByTierLevel();
        String newTier = calculateTier(userStatistic, allTiers);
        if (!newTier.equals(userStatistic.getCurrentTier())) {
            userStatistic.setCurrentTier(newTier);
            log.info("User {} tier updated: {} -> {}",
                    userStatistic.getUserId(), userStatistic.getCurrentTier(), newTier);
        }
        return newTier;
    }

    //사용자 현재 티어 정보 조회
    @Transactional(readOnly = true)
    public StatisticResponse.TierResponse getUserTierInfo(Long userId, UserStatisticEntity userStatistic) {
        TierInfoEntity currentTierInfo = tierInfoRepository.findByTierName(userStatistic.getCurrentTier())
                .orElseThrow(() -> new RuntimeException("현재 티어 정보를 찾을 수 없습니다."));

        Optional<TierInfoEntity> nextTierInfo = tierInfoRepository.findNextTier(currentTierInfo.getTierLevel());

        return StatisticResponse.TierResponse.builder()
                .currentTier(currentTierInfo.getTierName())
                .tierLevel(currentTierInfo.getTierLevel())
                .progress(calculateTierProgress(userStatistic, currentTierInfo, nextTierInfo.orElse(null)))
                .nextTier(nextTierInfo.map(this::convertToTierInfoResponse).orElse(null))
                .build();
    }

    //모든 티어 정보 조회
    @Transactional(readOnly = true)
    public List<StatisticResponse.TierInfoResponse> getAllTierInfo() {
        return tierInfoRepository.findAllOrderByTierLevel().stream()
                .map(this::convertToTierInfoResponse)
                .toList();
    }

    // Private 메서드들

    //사용자 통계 기반으로 적절한 티어 계산
    private String calculateTier(UserStatisticEntity userStatistic, List<TierInfoEntity> allTiers) {
        String currentTier = "뉴비"; // 기본값

        for (TierInfoEntity tier : allTiers) {
            if (isTierConditionMet(userStatistic, tier)) {
                currentTier = tier.getTierName();
            } else {
                break; // 조건을 만족하지 않으면 더 이상 확인하지 않음
            }
        }

        return currentTier;
    }

    //티어 승급 조건 만족 여부 체크
    private boolean isTierConditionMet(UserStatisticEntity userStatistic, TierInfoEntity tier) {
        // 1. 총 답변 수 조건 체크
        if (userStatistic.getTotalAnswerCount() < tier.getMinAnswerCount()) {
            return false;
        }

        // 2. 카테고리별 최소 답변 수 조건 체크 (조건이 있는 경우)
        if (tier.getMinCategoryAnswer() != null && tier.getMinCategoryAnswer() > 0) {
            if (!isAllCategoriesMinAnswersMet(userStatistic, (long) tier.getMinCategoryAnswer())) {
                return false;
            }
        }

        // 3. 평균 점수 조건 체크 (조건이 있는 경우)
        if (tier.getMinAverageScore() != null) {
            if (userStatistic.getAverageScore().compareTo(tier.getMinAverageScore()) < 0) {
                return false;
            }
        }

        return true;
    }

    //모든 카테고리의 최소 답변 수 조건 만족 여부 체크 (11개 카테고리)
    private boolean isAllCategoriesMinAnswersMet(UserStatisticEntity userStatistic, Long minCount) {
        return userStatistic.getDataStructureCount() >= minCount &&
                userStatistic.getComputerArchitectureCount() >= minCount &&
                userStatistic.getOperatingSystemCount() >= minCount &&
                userStatistic.getDatabaseCount() >= minCount &&
                userStatistic.getNetworkCount() >= minCount &&
                userStatistic.getSoftwareEngineeringCount() >= minCount &&
                userStatistic.getAlgorithmCount() >= minCount &&
                userStatistic.getDesignPatternCount() >= minCount &&
                userStatistic.getWebFrontendCount() >= minCount &&
                userStatistic.getWebBackendCount() >= minCount &&
                userStatistic.getCloudCount() >= minCount;
    }

    //티어 진행도 계산
    private StatisticResponse.TierProgressResponse calculateTierProgress(
            UserStatisticEntity userStatistic,
            TierInfoEntity currentTier,
            TierInfoEntity nextTier) {

        if (nextTier == null) {
            // 최고 티어인 경우
            return StatisticResponse.TierProgressResponse.builder()
                    .answerCount(StatisticResponse.ProgressItemResponse.builder()
                            .current(userStatistic.getTotalAnswerCount())
                            .required(currentTier.getMinAnswerCount())
                            .completed(true)
                            .build())
                    .categoryAnswers(StatisticResponse.ProgressItemResponse.builder()
                            .current(getMinCategoryAnswerCount(userStatistic))
                            .required(currentTier.getMinCategoryAnswer())
                            .completed(true)
                            .build())
                    .averageScore(StatisticResponse.ProgressItemResponse.builder()
                            .current(userStatistic.getAverageScore())
                            .required(currentTier.getMinAverageScore())
                            .completed(true)
                            .build())
                    .build();
        }

        // 다음 티어가 있는 경우
        long minCategoryCount = getMinCategoryAnswerCount(userStatistic);

        return StatisticResponse.TierProgressResponse.builder()
                .answerCount(StatisticResponse.ProgressItemResponse.builder()
                        .current(userStatistic.getTotalAnswerCount())
                        .required(nextTier.getMinAnswerCount())
                        .completed(userStatistic.getTotalAnswerCount() >= nextTier.getMinAnswerCount())
                        .build())
                .categoryAnswers(StatisticResponse.ProgressItemResponse.builder()
                        .current(minCategoryCount)
                        .required(nextTier.getMinCategoryAnswer())
                        .completed(minCategoryCount >= nextTier.getMinCategoryAnswer())
                        .build())
                .averageScore(StatisticResponse.ProgressItemResponse.builder()
                        .current(userStatistic.getAverageScore())
                        .required(nextTier.getMinAverageScore())
                        .completed(nextTier.getMinAverageScore() == null ||
                                userStatistic.getAverageScore().compareTo(nextTier.getMinAverageScore()) >= 0)
                        .build())
                .build();
    }

    //카테고리별 최소 답변 수 계산 (11개 카테고리 중 최솟값)
    private long getMinCategoryAnswerCount(UserStatisticEntity userStatistic) {
        return Math.min(
                Math.min(
                        Math.min(userStatistic.getDataStructureCount(), userStatistic.getComputerArchitectureCount()),
                        Math.min(userStatistic.getOperatingSystemCount(), userStatistic.getDatabaseCount())
                ),
                Math.min(
                        Math.min(
                                Math.min(userStatistic.getNetworkCount(), userStatistic.getSoftwareEngineeringCount()),
                                Math.min(userStatistic.getAlgorithmCount(), userStatistic.getDesignPatternCount())
                        ),
                        Math.min(
                                Math.min(userStatistic.getWebFrontendCount(), userStatistic.getWebBackendCount()),
                                userStatistic.getCloudCount()
                        )
                )
        );
    }


    //TierInfoEntity -> TierInfoResponse 변환
    private StatisticResponse.TierInfoResponse convertToTierInfoResponse(TierInfoEntity tierInfo) {
        return StatisticResponse.TierInfoResponse.builder()
                .tierName(tierInfo.getTierName())
                .tierLevel(tierInfo.getTierLevel())
                .requirements(StatisticResponse.TierRequirementsResponse.builder()
                        .answerCount(tierInfo.getMinAnswerCount())
                        .categoryAnswers(tierInfo.getMinCategoryAnswer())
                        .averageScore(tierInfo.getMinAverageScore())
                        .build())
                .description(tierInfo.getDescription())
                .build();
    }
}