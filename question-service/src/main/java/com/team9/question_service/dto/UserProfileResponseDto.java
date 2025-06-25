package com.team9.question_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * user-service로부터 받을 사용자 프로필 응답 DTO.
 * question-service는 이 중에서 interestCategories 필드만 사용합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    // user-service가 다른 필드(예: nickname, email 등)를 함께 보내주더라도,
    // question-service는 관심사 목록만 필요하므로 해당 필드만 정의합니다.
    // JSON 라이브러리(Jackson)는 DTO에 정의되지 않은 필드는 자동으로 무시합니다.
    private List<String> interestCategories;
}