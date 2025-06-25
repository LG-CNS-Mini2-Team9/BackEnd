package com.team9.ai_feedback_service.global.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AIQuestionErrorCode implements BaseErrorCode {

    AI_QUESTION_INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "AIQUESTION500", "AI로부터 올바르지 않은 응답을 받았습니다."),
    AI_QUESTION_API_FAILED(HttpStatus.BAD_GATEWAY, "AIQUESTION502", "AI API 호출에 실패했습니다.");

    private HttpStatus httpStatus;
    private String code;
    private String message;
}
