package com.team9.common.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}