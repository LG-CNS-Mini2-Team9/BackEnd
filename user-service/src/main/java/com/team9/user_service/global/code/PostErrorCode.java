package com.lgcns.backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements BaseErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST404", "게시글을 찾을 수 없습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "POST400", "유효하지 않은 카테고리입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "POST403", "작성자만 접근할 수 있습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION404", "질문을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
