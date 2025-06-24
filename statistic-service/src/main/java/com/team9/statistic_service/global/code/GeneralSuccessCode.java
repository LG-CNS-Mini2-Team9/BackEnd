package com.team9.statistic_service.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode {
    _OK(HttpStatus.OK, "COMMON_200_1", "성공"),
    _CREATED(HttpStatus.CREATED, "COMMON_201_1", "생성 성공"),
    _ACCEPTED(HttpStatus.ACCEPTED, "COMMON_202_1", "요청 접수"),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON_204_1", "성공 (데이터 없음)"),
    _DELETED(HttpStatus.OK, "COMMON_200_2", "삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
