package com.team9.statistic_service.service;
import com.team9.statistic_service.domain.entity.ActivityRecordEntity;
import com.team9.statistic_service.domain.repository.ActivityRecordRepository;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GrassService {

    private final ActivityRecordRepository activityRecordRepository;

    //활동 기록 업데이트 (하루 1회 답변 시 잔디 활성화)
    public void updateActivityRecord(Long userId, LocalDate activityDate) {
        ActivityRecordEntity activityRecord = activityRecordRepository
                .findByUserIdAndActivityDate(userId, activityDate)
                .orElse(ActivityRecordEntity.builder()
                        .userId(userId)
                        .activityDate(activityDate)
                        .isActive(false)
                        .answerCount(0) // Integer 타입 사용
                        .build());

        // 답변 수 증가
        activityRecord.setAnswerCount(activityRecord.getAnswerCount() + 1);

        // 하루에 1개 이상 답변하면 잔디 활성화
        if (activityRecord.getAnswerCount() >= 1) {
            activityRecord.setIsActive(true);
        }

        activityRecordRepository.save(activityRecord);

        log.debug("Activity record updated for user {} on {}: {} answers",
                userId, activityDate, activityRecord.getAnswerCount());
    }

    // 사용자의 잔디 데이터 조회 (현재 연도)
    @Transactional(readOnly = true)
    public StatisticResponse.GrassResponse getCurrentYearGrassData(Long userId) {
        return getGrassDataByYear(userId, LocalDate.now().getYear());
    }

    //사용자의 특정 연도 잔디 데이터 조회
    @Transactional(readOnly = true)
    public StatisticResponse.GrassResponse getGrassDataByYear(Long userId, int year) {
        List<ActivityRecordEntity> activities = activityRecordRepository.findByUserIdAndYear(userId, year);
        Long totalActiveDays = activityRecordRepository.countActiveRecordsByUserId(userId);
        List<StatisticResponse.ActivityResponse> activityResponses = activities.stream()
                .map(this::convertToActivityResponse)
                .collect(Collectors.toList());

        // 연속 활동 일수 계산
        int currentStreak = calculateCurrentStreak(userId);
        int longestStreak = calculateLongestStreak(activities);

        return StatisticResponse.GrassResponse.builder()
                .year(year)
                .totalActiveDays(totalActiveDays)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .activities(activityResponses)
                .build();
    }

    //현재 연속 활동 일수 계산
    @Transactional(readOnly = true)
    public int calculateCurrentStreak(Long userId) {
        // 실제 Repository 메서드 사용
        List<ActivityRecordEntity> recentRecords =
                activityRecordRepository.findActiveRecordsByUserIdOrderByDateDesc(userId);

        if (recentRecords.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate expectedDate = LocalDate.now();

        // 오늘부터 역순으로 연속 활동 확인
        for (ActivityRecordEntity record : recentRecords) {
            if (record.getActivityDate().equals(expectedDate) ||
                    record.getActivityDate().equals(expectedDate.minusDays(1))) {
                streak++;
                expectedDate = record.getActivityDate().minusDays(1);
            } else if (record.getActivityDate().isBefore(expectedDate.minusDays(1))) {
                break; // 연속이 끊어지면 중단
            }
        }

        return streak;
    }

    // Private 메서드들

    //ActivityRecordEntity를 ActivityResponse로 변환
    private StatisticResponse.ActivityResponse convertToActivityResponse(ActivityRecordEntity activity) {
        return StatisticResponse.ActivityResponse.builder()
                .date(activity.getActivityDate())
                .answerCount(activity.getAnswerCount()) // Integer 타입 그대로 사용
                .isActive(activity.getIsActive())
                .build();
    }

    //최장 연속 활동 일수 계산
    private int calculateLongestStreak(List<ActivityRecordEntity> activities) {
        if (activities.isEmpty()) return 0;

        // 날짜순으로 정렬
        activities.sort((a, b)
                -> a.getActivityDate().compareTo(b.getActivityDate()));
        int longestStreak = 0;
        int currentStreak = 0;
        LocalDate previousDate = null;

        for (ActivityRecordEntity activity : activities) {
            if (!activity.getIsActive()) {
                currentStreak = 0;
                previousDate = activity.getActivityDate();
                continue;
            }

            if (previousDate == null || activity.getActivityDate().equals(previousDate.plusDays(1))) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 1;
            }

            previousDate = activity.getActivityDate();
        }

        return longestStreak;
    }
}