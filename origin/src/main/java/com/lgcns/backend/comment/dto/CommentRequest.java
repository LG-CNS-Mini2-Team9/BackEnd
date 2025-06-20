package com.lgcns.backend.comment.dto;

import lombok.Builder;
import lombok.Getter;

public class CommentRequest {

    @Builder
    @Getter
    public static class CommentCreateRequest {
        private String content;
    }

    @Builder
    @Getter
    public static class CommentUpdateRequest {
        private String content;
    }
}
