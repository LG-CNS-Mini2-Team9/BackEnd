package com.team9.statistic_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierInfo {
    private String currentTier;        // 현재 티어 (뉴비, 루키, 에이스, 마스터, 레전드)
    private String nextTier;           // 다음 티어
    private double progressPercent;    // 다음 티어까지 달성도 (0-100)
    private String progressMessage;    // 달성도 메시지

    public static TierInfo createMaxTier() {
        return TierInfo.builder()
                .currentTier("레전드")
                .nextTier(null)
                .progressPercent(100.0)
                .progressMessage("최고 티어에 도달했습니다!")
                .build();
    }
}