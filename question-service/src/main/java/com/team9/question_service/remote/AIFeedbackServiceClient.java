package com.team9.question_service.remote;

import com.team9.question_service.global.response.CustomResponse; // common 모듈의 CustomResponse 사용 (만약 없다면 새로 정의 필요)
import com.team9.question_service.dto.AiCreatedQuestionDto;
import com.team9.question_service.dto.AiGenerationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ai-feedback-service")
public interface AIFeedbackServiceClient {

    /**
     * ai-feedback-service에 질문 생성을 요청하는 내부 API를 호출합니다.
     * @param request 카테고리와 기존 키워드 목록이 담긴 요청 DTO
     * @return 생성된 질문 정보가 담긴 응답 DTO
     */
    @PostMapping("/internal/ai/questions") // 1. 경로를 "/internal/ai/questions"로 수정
    CustomResponse<AiCreatedQuestionDto> generateQuestion(@RequestBody AiGenerationRequestDto request); // 2. 반환 타입을 CustomResponse로 수정
}