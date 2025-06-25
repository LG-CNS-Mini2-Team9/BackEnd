package com.team9.question_service.dto;

import com.team9.common.domain.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternalQuestionResponse {
    private Long id;
    private String content;
    private Category category;
}
