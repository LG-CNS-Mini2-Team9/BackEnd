package com.lgcns.backend.csanswer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CSAnswerRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CSAnswerCreateRequest {
        private Long csquestion_id;
        private String csanswer_content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CSAnswerUpdate{
        private String csanswer_content;
    }
}
