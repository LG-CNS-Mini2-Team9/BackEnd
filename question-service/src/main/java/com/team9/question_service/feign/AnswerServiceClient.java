package com.team9.question_service.feign;

import com.team9.common.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Eureka에 등록된 'answer-service'를 호출하기 위한 Feign Client 인터페이스입니다.
 * Spring Cloud가 이 인터페이스의 구현체를 자동으로 생성해 줍니다.
 */
@FeignClient(name = "answer-service")
public interface AnswerServiceClient {
    // 임시 명세입니다...
    /**
     * answer-service에 구현될 API 명세입니다.
     * 특정 사용자가 답변한 모든 질문의 ID 목록을 반환하는 API를 호출합니다.
     * @param userId 조회할 사용자의 ID
     * @return 사용자가 답변한 질문 ID 목록을 담은 CustomResponse
     */
    @GetMapping("/internal/answers/submitted-ids")
    CustomResponse<List<Long>> getSubmittedQuestionIdsByUserId(@RequestParam("userId") Long userId);

}