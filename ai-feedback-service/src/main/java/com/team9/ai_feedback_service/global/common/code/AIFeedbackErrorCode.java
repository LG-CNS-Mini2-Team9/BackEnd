package com.team9.ai_feedback_service.global.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AIFeedbackErrorCode implements BaseErrorCode {

    AI_FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "AIFEEDBACK404", "피드백을 찾을 수 없습니다."),
    AI_FEEDBACK_INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "AIFEEDBACK500", "AI로부터 올바르지 않은 응답을 받았습니다."),
    AI_FEEDBACK_API_FAILED(HttpStatus.BAD_GATEWAY, "AIFEEDBACK502", "AI API 호출에 실패했습니다.");

    private HttpStatus httpStatus;
    private String code;
    private String message;
}
