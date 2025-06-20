package com.lgcns.backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "댓글을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "COMMENT403", "작성자만 접근할 수 있습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST404", "게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.FORBIDDEN, "USER403", "인증된 사용자를 찾을 수 없습니다."),
    NO_UPDATE_PERMISSION(HttpStatus.FORBIDDEN, "COMMENT403_1", "댓글 수정 권한이 없습니다."),
    NO_DELETE_PERMISSION(HttpStatus.FORBIDDEN, "COMMENT403_2", "댓글 삭제 권한이 없습니다.");

    private HttpStatus httpStatus;
    private String code;
    private String message;

}
