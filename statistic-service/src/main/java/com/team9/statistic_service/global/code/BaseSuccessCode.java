package com.team9.statistic_service.global.code;

import org.springframework.http.HttpStatus;

public interface BaseSuccessCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
