package com.team9.question_service.remote;

import com.team9.common.response.CustomResponse; // common 모듈의 응답 객체
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * answer-service와의 내부 통신을 위한 Feign Client 인터페이스
 */
@FeignClient("answer-service")
public interface AnswerServiceClient {

    /**
     * answer-service의 내부 API를 호출하여 특정 사용자가 답변한 모든 질문의 ID 목록을 가져옵니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return CustomResponse 형태의 응답. 결과 데이터는 Long 타입의 리스트입니다.
     */
    @GetMapping("/internal/answers/submitted-ids")
    ResponseEntity<CustomResponse<List<Long>>> getSubmittedQuestionIdsByUserId(@RequestParam("userId") Long userId);
}