package com.team9.answer_service.remote.ai;

import com.team9.answer_service.remote.ai.dto.FeedbackRequestDto;
import com.team9.answer_service.remote.ai.dto.FeedbackScoreResponseDto;
import com.team9.answer_service.remote.csquestion.dto.CSQuestionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-feedback-service", path="/internal/ai/feedbacks")
public interface RemoteAIFeedbackService {

    @PostMapping()
    FeedbackScoreResponseDto createFeedback(@RequestBody FeedbackRequestDto feedbackRequestDto);
}
