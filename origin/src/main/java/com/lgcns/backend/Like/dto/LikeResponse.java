package com.lgcns.backend.Like.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeResponse {
    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("is_liked")
    private Boolean isLiked;

    public static LikeResponse from(Long commentId, boolean isLiked) {
        return LikeResponse.builder()
                .commentId(commentId)
                .isLiked(isLiked)
                .build();
    }
}
