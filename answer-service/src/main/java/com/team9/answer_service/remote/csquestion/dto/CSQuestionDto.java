package com.team9.answer_service.remote.csquestion.dto;

import com.team9.answer_service.global.domain.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CSQuestionDto {

    @Getter
    @Setter
    public class Response{
        private Long id;
        private String content;
        private Category category;
    }
}
