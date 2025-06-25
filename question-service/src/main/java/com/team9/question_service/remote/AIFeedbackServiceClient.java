package com.team9.question_service.remote;

import com.team9.common.dto.ApiResponseDto; // ai-feedback-service의 응답 DTO
import com.team9.question_service.dto.AiCreatedQuestionDto;
import com.team9.question_service.dto.AiGenerationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Eureka에 등록된 "ai-feedback-service"를 찾아가도록 설정
@FeignClient("ai-feedback-service")
public interface AIFeedbackServiceClient {

    /**
     * ai-feedback-service에 질문 생성을 요청하는 내부 API를 호출합니다.
     * @param request 카테고리와 기존 키워드 목록이 담긴 요청 DTO
     * @return 생성된 질문 정보가 담긴 응답 DTO
     */
    @PostMapping("/backend/ai/questions")
    ApiResponseDto<AiCreatedQuestionDto> generateQuestion(@RequestBody AiGenerationRequestDto request);
}