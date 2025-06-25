package com.team9.statistic_service.api.open;

import com.team9.statistic_service.domain.dto.ApiResponseDto;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import com.team9.statistic_service.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    /**
     * 사용자 통계 조회
     * 프론트엔드에서 호출하는 메인 API
     *
     * Authorization 헤더를 통해 사용자 인증 정보를 받고,
     * answer-service로 전달하여 해당 사용자의 답변 데이터를 조회
     */
    @GetMapping("/me")
    public ApiResponseDto<StatisticResponse> getMyStatistics(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userName", required = false, defaultValue = "사용자") String userName) {

        log.info("사용자 통계 조회 요청 - userName: {}", userName);

        try {
            StatisticResponse statistics = statisticService.getUserStatistics(authorization, userName);

            log.info("통계 조회 완료 - 총 답변 수: {}, 평균 점수: {}, 티어: {}",
                    statistics.getTotalAnswerCount(),
                    statistics.getTotalAverageScore(),
                    statistics.getTierInfo().getCurrentTier());

            return ApiResponseDto.createOk(statistics);

        } catch (Exception e) {
            log.error("통계 조회 중 오류 발생", e);
            throw new RuntimeException("통계 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 헬스 체크 API
     */
    @GetMapping("/health")
    public ApiResponseDto<String> healthCheck() {
        return ApiResponseDto.createOk("Statistic Service is running!");
    }
}