package com.team9.statistic_service.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode  implements BaseErrorCode{

    // 400 Bad Request
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400_1", "잘못된 요청입니다."),

    // 401 Unauthorized
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401_1", "인증이 필요합니다."),

    // 403 Forbidden
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403_1", "접근 권한이 없습니다."),

    // 404 Not Found
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404_1", "요청한 리소스를 찾을 수 없습니다."),

    // 409 Conflict
    _CONFLICT(HttpStatus.CONFLICT, "COMMON_409_1", "요청이 현재 서버 상태와 충돌합니다."),

    // 500 Internal Server Error
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_1", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
