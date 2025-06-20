package com.lgcns.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lgcns.backend.global.domain.Category;
import lombok.Builder;
import lombok.Getter;

//게시글 작성 또는 수정 시, 요청 데이터를 받을 때 사용
public class PostRequest {

    @Getter
    @Builder
    public static class PostCreateRequest {
        private String title;

        @JsonProperty("question_id")
        private Long questionId;
        private String content;
        private String category;
    }

    @Getter
    @Builder
    public static class PostUpdateRequest {
        private String title;

        @JsonProperty("question_id")
        private Long questionId;
        private String content;
        private String category;
    }


}