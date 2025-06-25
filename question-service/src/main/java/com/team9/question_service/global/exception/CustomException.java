package com.team9.question_service.global.exception;

import com.team9.question_service.global.code.BaseErrorCode;
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

}
