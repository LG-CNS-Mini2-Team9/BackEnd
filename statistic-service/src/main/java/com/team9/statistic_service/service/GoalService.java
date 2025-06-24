package com.team9.statistic_service.service;

import com.team9.statistic_service.domain.entity.GoalEntity;
import com.team9.statistic_service.domain.repository.GoalRepository;
import com.team9.statistic_service.domain.dto.StatisticRequest;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import com.team9.statistic_service.global.exception.CustomException;
import com.team9.statistic_service.global.code.StatisticErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;

    //목표 생성 (userId를 파라미터로 받음)
    public StatisticResponse.GoalResponse createGoal(Long userId, StatisticRequest.GoalCreateRequest request) {
        // 기존 활성 목표가 있는지 확인 (실제 Repository 메서드 사용)
        Optional<GoalEntity> existingGoal = goalRepository.findActiveGoalByUserIdAndType(userId, request.getGoalType());
        if (existingGoal.isPresent()) {
            throw new CustomException(StatisticErrorCode.GOAL_ALREADY_EXISTS);
        }

        LocalDate endDate = calculateEndDate(request.getStartDate(), request.getGoalType());

        GoalEntity goal = GoalEntity.builder()
                .userId(userId)
                .goalType(request.getGoalType())
                .targetCount(request.getTargetCount()) // Integer 타입
                .currentCount(0) // Integer 타입
                .startDate(request.getStartDate())
                .endDate(endDate)
                .isAchieved(false)
                .isActive(true)
                .build();

        GoalEntity savedGoal = goalRepository.save(goal);

        log.info("Goal created for user {}: {} goal with target {}",
                userId, request.getGoalType(), request.getTargetCount());

        return convertToGoalResponse(savedGoal);
    }

    //사용자의 활성 목표 조회
    @Transactional(readOnly = true)
    public List<StatisticResponse.GoalResponse> getActiveGoals(Long userId) {
        List<GoalEntity> activeGoals = goalRepository.findActiveGoalsByUserId(userId);
        return activeGoals.stream()
                .map(this::convertToGoalResponse)
                .collect(Collectors.toList());
    }

    //목표 수정
    public StatisticResponse.GoalResponse updateGoal(Long goalId, StatisticRequest.GoalUpdateRequest request) {
        GoalEntity goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(StatisticErrorCode.GOAL_NOT_FOUND));

        // 이미 달성된 목표는 수정 불가
        if (goal.getIsAchieved()) {
            throw new CustomException(StatisticErrorCode.GOAL_ALREADY_EXISTS, "이미 달성된 목표는 수정할 수 없습니다.");
        }

        // 만료된 목표는 수정 불가
        if (!goal.getIsActive() || goal.getEndDate().isBefore(LocalDate.now())) {
            throw new CustomException(StatisticErrorCode.INVALID_GOAL_DATE, "만료된 목표는 수정할 수 없습니다.");
        }

        if (request.getTargetCount() != null) {
            goal.setTargetCount(request.getTargetCount());
        }

        if (request.getEndDate() != null) {
            goal.setEndDate(request.getEndDate());
        }

        if (request.getIsActive() != null) {
            goal.setIsActive(request.getIsActive());
        }

        goalRepository.save(goal);

        log.info("Goal {} updated", goalId);

        return convertToGoalResponse(goal);
    }

    //목표 삭제 (비활성화)
    public void deleteGoal(Long goalId) {
        GoalEntity goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(StatisticErrorCode.GOAL_NOT_FOUND));

        goal.setIsActive(false);
        goalRepository.save(goal);

        log.info("Goal {} deactivated", goalId);
    }

    //달성된 목표 조회
    @Transactional(readOnly = true)
    public List<StatisticResponse.GoalResponse> getAchievedGoals(Long userId) {
        List<GoalEntity> achievedGoals = goalRepository.findAchievedGoalsByUserId(userId);
        return achievedGoals.stream()
                .map(this::convertToGoalResponse)
                .collect(Collectors.toList());
    }


    // 스케줄링으로 만료된 목표 자동 비활성화 (매일 자정 실행)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateExpiredGoals() {
        LocalDate today = LocalDate.now();

        // 모든 활성 목표를 조회해서 만료된 것들 찾기
        List<GoalEntity> allActiveGoals = goalRepository.findAll().stream()
                .filter(goal -> goal.getIsActive() && goal.getEndDate().isBefore(today))
                .toList();

        for (GoalEntity goal : allActiveGoals) {
            goal.setIsActive(false);
            goalRepository.save(goal);
            log.info("Goal {} automatically deactivated due to expiration", goal.getId());
        }

        if (!allActiveGoals.isEmpty()) {
            log.info("Deactivated {} expired goals", allActiveGoals.size());
        }
    }

    // Package-private 메서드 (StatisticService에서 호출)

    // 목표 진행도 업데이트 (답변 저장 시 호출)
        boolean updateGoalProgress(Long userId, LocalDate answerDate) {
        List<GoalEntity> activeGoals = goalRepository.findActiveGoalsByUserIdAndDate(userId, answerDate);
        boolean goalAchieved = false;

        for (GoalEntity goal : activeGoals) {
            goal.setCurrentCount(goal.getCurrentCount() + 1);

            if (goal.getCurrentCount() >= goal.getTargetCount() && !goal.getIsAchieved()) {
                goal.setIsAchieved(true);
                goalAchieved = true;
                log.info("Goal achieved for user {}: {}", userId, goal.getId());
            }

            goalRepository.save(goal);
        }

        return goalAchieved;
    }

    // Private 메서드들


    //목표 종료일 계산
    private LocalDate calculateEndDate(LocalDate startDate, GoalEntity.GoalType goalType) {
        return switch (goalType) {
            case DAILY -> startDate; // 일일 목표는 당일까지
            case WEEKLY -> startDate.plusDays(6); // 주간 목표는 시작일부터 6일 후 (7일간)
        };
    }


     // GoalEntity를 GoalResponse로 변환 (실제 Response 구조 사용)
    private StatisticResponse.GoalResponse convertToGoalResponse(GoalEntity goal) {
        // 진행률 계산 (Integer 타입 처리)
        double progressPercentage = goal.getTargetCount() > 0
                ? (goal.getCurrentCount().doubleValue() / goal.getTargetCount().doubleValue()) * 100
                : 0.0;
        progressPercentage = Math.min(progressPercentage, 100.0); // 100% 초과 방지

        return StatisticResponse.GoalResponse.builder()
                .id(goal.getId())
                .goalType(goal.getGoalType())
                .targetCount(goal.getTargetCount())
                .currentCount(goal.getCurrentCount())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .isAchieved(goal.getIsAchieved())
                .isActive(goal.getIsActive())
                .progressPercentage(progressPercentage)
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}