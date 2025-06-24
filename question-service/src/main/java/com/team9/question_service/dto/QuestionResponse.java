package com.team9.question_service.dto;

import com.team9.common.domain.Category;
import com.team9.question_service.domain.Question; // 이 줄을 추가해주세요.
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private Category category;
    private LocalDateTime createdAt;
    private String content;
    private String hint;
    private boolean isSubmitted;

    /**
     * Question 엔티티와 제출 여부를 받아 DTO를 생성하는 정적 팩토리 메서드.
     * 변환 로직을 DTO 내부에 캡슐화하여 서비스 코드의 가독성을 높입니다.
     *
     * @param question      원본 Question 엔티티
     * @param isSubmitted   사용자의 제출 여부
     * @return 변환된 QuestionResponse DTO
     */
    public static QuestionResponse from(Question question, boolean isSubmitted) {
        return QuestionResponse.builder()
                .id(question.getId())
                .category(question.getCategory())
                .createdAt(question.getCreatedAt())
                .content(question.getContent())
                .hint(question.getHint())
                .isSubmitted(isSubmitted)
                .build();
    }
}