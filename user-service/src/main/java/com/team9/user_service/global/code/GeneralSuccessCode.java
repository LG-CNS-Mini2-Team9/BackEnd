package com.lgcns.backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성되었습니다."),
    _DELETED(HttpStatus.NO_CONTENT, "COMMON204", "성공적으로 삭제했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}