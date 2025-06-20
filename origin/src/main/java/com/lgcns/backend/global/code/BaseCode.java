package com.lgcns.backend.global.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}