package com.team9.statistic_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrassData {
    private Map<String, Boolean> dailyActivity;  // "2025-06-26" -> true/false
    private int maxConsecutiveDays;              // 최장 연속 학습일
    private int currentStreak;                   // 현재 연속 학습일
    private List<String> activeDates;            // 활동한 날짜들
}