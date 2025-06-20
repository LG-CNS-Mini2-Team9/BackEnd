package com.lgcns.backend.global.exception;

import com.lgcns.backend.global.code.BaseErrorCode;
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
