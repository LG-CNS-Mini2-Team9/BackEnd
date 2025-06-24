package com.team9.statistic_service.api.internal;

import com.team9.statistic_service.domain.dto.StatisticRequest;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import com.team9.statistic_service.service.StatisticService;
import com.team9.statistic_service.global.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticInternalController {

    private final StatisticService statisticService;

    // 답변 저장 시 통계 업데이트 (answer-service에서 호출)
    @PostMapping("/update/answer")
    public ResponseEntity<CustomResponse<StatisticResponse.StatisticUpdateResponse>> updateAnswerStatistic(
            @Valid @RequestBody StatisticRequest.AnswerUpdateRequest request) {

        log.info("Updating answer statistic for user: {}, category: {}, score: {}",
                request.getUserId(), request.getCategory(), request.getScore());

        StatisticResponse.StatisticUpdateResponse response =
                statisticService.updateAnswerStatistic(request);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 점수 업데이트 (ai-feedback-service에서 호출)
    @PutMapping("/update/score")
    public ResponseEntity<CustomResponse<StatisticResponse.StatisticUpdateResponse>> updateScoreStatistic(
            @Valid @RequestBody StatisticRequest.ScoreUpdateRequest request) {

        log.info("Updating score statistic for user: {}, answer: {}, category: {}, newScore: {}",
                request.getUserId(), request.getAnswerId(), request.getCategory(), request.getNewScore());

        StatisticResponse.StatisticUpdateResponse response =
                statisticService.updateScoreStatistic(request);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 사용자 통계 초기화 (user-service에서 사용자 생성 시 호출)
    @PostMapping("/init/{userId}")
    public ResponseEntity<CustomResponse<String>> initializeUserStatistic(@PathVariable Long userId) {

        log.info("Initializing statistic for new user: {}", userId);

        // 사용자 통계 초기화 (getUserStatisticOrCreate 호출하면 자동 생성됨)
        statisticService.getStatisticSummary(userId);

        return ResponseEntity.ok(CustomResponse.ok("사용자 통계가 초기화되었습니다."));
    }
}
