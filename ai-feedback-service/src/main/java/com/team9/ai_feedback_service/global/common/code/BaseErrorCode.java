package com.team9.ai_feedback_service.global.common.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}