package com.team9.statistic_service.global.exception;

import com.team9.statistic_service.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final BaseErrorCode baseErrorCode;

    public CustomException(BaseErrorCode baseErrorCode, String message) {
        super(message);
        this.baseErrorCode = baseErrorCode;
    }
}
