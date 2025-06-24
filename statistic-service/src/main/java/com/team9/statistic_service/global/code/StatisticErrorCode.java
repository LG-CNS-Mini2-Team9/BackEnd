package com.team9.statistic_service.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StatisticErrorCode implements BaseErrorCode{
    // 통계 관련 에러
    STATISTIC_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTIC_404_1", "사용자 통계를 찾을 수 없습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "STATISTIC_400_1", "유효하지 않은 카테고리입니다."),
    STATISTIC_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STATISTIC_500_1", "통계 업데이트에 실패했습니다."),

    // 티어 관련 에러
    TIER_NOT_FOUND(HttpStatus.NOT_FOUND, "TIER_404_1", "티어 정보를 찾을 수 없습니다."),
    TIER_CALCULATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TIER_500_1", "티어 계산 중 오류가 발생했습니다."),

    // 목표 관련 에러
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "GOAL_404_1", "목표를 찾을 수 없습니다."),
    GOAL_ALREADY_EXISTS(HttpStatus.CONFLICT, "GOAL_409_1", "해당 타입의 활성 목표가 이미 존재합니다."),
    GOAL_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "GOAL_500_1", "목표 생성에 실패했습니다."),
    INVALID_GOAL_TYPE(HttpStatus.BAD_REQUEST, "GOAL_400_1", "유효하지 않은 목표 타입입니다."),
    INVALID_GOAL_DATE(HttpStatus.BAD_REQUEST, "GOAL_400_2", "목표 날짜가 유효하지 않습니다."),

    // 잔디(활동) 관련 에러
    ACTIVITY_RECORD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ACTIVITY_500_1", "활동 기록 처리 중 오류가 발생했습니다."),
    INVALID_ACTIVITY_DATE(HttpStatus.BAD_REQUEST, "ACTIVITY_400_1", "유효하지 않은 활동 날짜입니다."),

    // 일반적인 에러
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "USER_400_1", "유효하지 않은 사용자 ID입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "AUTH_401_1", "권한이 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "AUTH_403_1", "접근이 금지되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
