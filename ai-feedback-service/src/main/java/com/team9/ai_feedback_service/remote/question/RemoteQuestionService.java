package com.team9.ai_feedback_service.remote.question;

import com.team9.ai_feedback_service.remote.question.dto.QuestionResponse;
import com.team9.common.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "question-service", path = "/api/questions")
public interface RemoteQuestionService {

    @GetMapping("/{id}")
    CustomResponse<QuestionResponse> getQuestionDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId);
}
