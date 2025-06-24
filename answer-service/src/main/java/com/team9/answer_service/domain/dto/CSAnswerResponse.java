package com.team9.answer_service.domain.dto;

import java.time.LocalDateTime;

import com.team9.answer_service.global.domain.Category;

import lombok.Builder;
import lombok.Data;

public class CSAnswerResponse {

    @Data
    @Builder
    public static class CSAnswerListResponse {
        private String user_nickname;
        private Long csquestion_id;
        private Category csquestion_category;
        private String csquestion_content;
        private Long csanswer_id;
        private String csanswer_content;
        private Long csanswer_score;
        private LocalDateTime csanswer_created_at;
        private boolean canAccess;
    }

    @Data
    @Builder
    public static class CSAnswerDetailResponse {
        private String user_nickname;
        private Long user_id;
        private Long csquestion_id;
        private Category csquestion_category;
        private String csquestion_content;
        private Long csanswer_id;
        private String csanswer_content;
        private Long csanswer_score;
        private LocalDateTime csanswer_created_at;
    }


    @Data
    @Builder
    public static class CSStatisticResponse{
        private Long csquestion_id;
        private Category csquestion_category;
        private Long csanswer_id;
        private Long csanswer_score;
    }

//    @Data
//    @Builder
//    public static class AIFeedbackResponse {
//        private Long csanswer_id;
//        private String csanswer_feedback;
//    }
}
