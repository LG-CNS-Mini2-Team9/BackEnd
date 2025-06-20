package com.lgcns.backend.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lgcns.backend.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Getter
    @Builder
    public static class CommentCreateResponse{
        private Long id;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("post_id")
        private Long postId;

        private String content;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public static CommentCreateResponse from(Comment comment) {
            return CommentCreateResponse.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .postId(comment.getPost().getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommentUpdateResponse{
        private Long id;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("post_id")
        private Long postId;

        private String content;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public static CommentUpdateResponse from(Comment comment) {
            return CommentUpdateResponse.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .postId(comment.getPost().getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommentListResponse {

        private List<CommentSummary> comments;

        @Getter
        @Builder
        public static class CommentSummary {

            @JsonProperty("comment_id")
            private Long commentId;

            private String username;
            private String content;

            @JsonProperty("created_at")
            private LocalDateTime createdAt;

            public static CommentSummary fromEntity(Comment comment){
                return CommentSummary.builder()
                        .commentId(comment.getId())
                        .username(comment.getUser().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build();
            }
        }

        public static CommentListResponse from(List<Comment> comments) {
            List<CommentSummary> commentSummaries = comments.stream()
                    .map(CommentSummary::fromEntity)
                    .toList();

            return CommentListResponse.builder()
                    .comments(commentSummaries)
                    .build();
        }
    }
}

