package com.team9.ai_feedback_service.global.common.exception;

import com.team9.ai_feedback_service.global.common.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }

    public static CustomException of(BaseErrorCode errorCode) {
        return new CustomException(errorCode);
    }

}
