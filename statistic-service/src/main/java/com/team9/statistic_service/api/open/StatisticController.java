package com.team9.statistic_service.api.open;

import com.team9.statistic_service.domain.dto.StatisticRequest;
import com.team9.statistic_service.domain.dto.StatisticResponse;
import com.team9.statistic_service.service.StatisticService;
import com.team9.statistic_service.service.GrassService;
import com.team9.statistic_service.service.TierService;
import com.team9.statistic_service.service.GoalService;
import com.team9.statistic_service.global.response.CustomResponse;
import com.team9.statistic_service.global.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;
    private final GrassService grassService;
    private final TierService tierService;
    private final GoalService goalService;

    // 사용자 통계 요약 조회
    @GetMapping("/summary")
    public ResponseEntity<CustomResponse<StatisticResponse.StatisticSummaryResponse>> getStatisticSummary(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        StatisticResponse.StatisticSummaryResponse response = statisticService.getStatisticSummary(userId);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 사용자 상세 통계 조회 (요약과 동일하지만 확장 가능)
    @GetMapping("/detail")
    public ResponseEntity<CustomResponse<StatisticResponse.StatisticSummaryResponse>> getStatisticDetail(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        StatisticResponse.StatisticSummaryResponse response = statisticService.getStatisticSummary(userId);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 현재 연도 잔디 데이터 조회
    @GetMapping("/grass")
    public ResponseEntity<CustomResponse<StatisticResponse.GrassResponse>> getCurrentYearGrassData(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        StatisticResponse.GrassResponse response = grassService.getCurrentYearGrassData(userId);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 특정 연도 잔디 데이터 조회
    @GetMapping("/grass/{year}")
    public ResponseEntity<CustomResponse<StatisticResponse.GrassResponse>> getGrassDataByYear(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int year) {

        Long userId = getUserIdFromDetails(userDetails);
        StatisticResponse.GrassResponse response = grassService.getGrassDataByYear(userId, year);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 사용자 현재 티어 정보
    @GetMapping("/tier")
    public ResponseEntity<CustomResponse<StatisticResponse.TierResponse>> getUserTierInfo(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        // StatisticService에서 UserStatisticEntity를 가져와서 TierService에 전달
        var userStatistic = statisticService.getUserStatisticOrCreate(userId);
        StatisticResponse.TierResponse response = tierService.getUserTierInfo(userId, userStatistic);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 모든 티어 정보 조회
    @GetMapping("/tier/all")
    public ResponseEntity<CustomResponse<List<StatisticResponse.TierInfoResponse>>> getAllTierInfo() {

        List<StatisticResponse.TierInfoResponse> response = tierService.getAllTierInfo();

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 목표 설정
    @PostMapping("/goal")
    public ResponseEntity<CustomResponse<StatisticResponse.GoalResponse>> createGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody StatisticRequest.GoalCreateRequest request) {

        Long userId = getUserIdFromDetails(userDetails);
        StatisticResponse.GoalResponse response = goalService.createGoal(userId, request);

        return ResponseEntity.ok(CustomResponse.created(response));
    }

    // 활성 목표 조회
    @GetMapping("/goal")
    public ResponseEntity<CustomResponse<List<StatisticResponse.GoalResponse>>> getActiveGoals(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        List<StatisticResponse.GoalResponse> response = goalService.getActiveGoals(userId);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 목표 수정
    @PutMapping("/goal/{goalId}")
    public ResponseEntity<CustomResponse<StatisticResponse.GoalResponse>> updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody StatisticRequest.GoalUpdateRequest request) {

        StatisticResponse.GoalResponse response = goalService.updateGoal(goalId, request);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 목표 삭제
    @DeleteMapping("/goal/{goalId}")
    public ResponseEntity<CustomResponse<Void>> deleteGoal(@PathVariable Long goalId) {

        goalService.deleteGoal(goalId);

        return ResponseEntity.ok(CustomResponse.success(GeneralSuccessCode._DELETED, null));
    }

    // 달성된 목표 조회
    @GetMapping("/goal/achieved")
    public ResponseEntity<CustomResponse<List<StatisticResponse.GoalResponse>>> getAchievedGoals(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        List<StatisticResponse.GoalResponse> response = goalService.getAchievedGoals(userId);

        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // UserDetails에서 사용자 ID 추출 (JWT 토큰에서)
    private Long getUserIdFromDetails(UserDetails userDetails) {
        // 실제 구현에서는 JWT에서 사용자 ID를 추출하는 로직 필요
        // 현재는 username을 ID로 가정 (실제로는 user-service와 연동 필요)
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID in token");
        }
    }
}
