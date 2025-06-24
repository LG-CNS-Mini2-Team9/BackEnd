package com.team9.ai_feedback_service.api.backend;

import com.team9.ai_feedback_service.domain.dto.response.FeedbackScoreResponseDto;
import com.team9.ai_feedback_service.service.AIFeedbackService;
import com.team9.common.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/backend/ai", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class AIFeedbackController {

    private final AIFeedbackService aiFeedbackService;

    @GetMapping("/feedback/{answerId}/score")
    public ApiResponseDto<FeedbackScoreResponseDto> getFeedbackScore(@PathVariable Long answerId) {
        return ApiResponseDto.createOk(aiFeedbackService.getFeedbackScore(answerId));
    }

}
