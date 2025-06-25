package com.team9.ai_feedback_service.api.open;

import com.team9.ai_feedback_service.domain.dto.response.AIFeedbackResponseDto;
import com.team9.ai_feedback_service.domain.dto.response.FeedbackResponseDto;
import com.team9.ai_feedback_service.global.common.response.CustomResponse;
import com.team9.ai_feedback_service.service.AIFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/api/ai/feedbacks", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AIFeedbackController {

    private final AIFeedbackService aiFeedbackService;

    @GetMapping("/{answerId}")
    public ResponseEntity<CustomResponse<FeedbackResponseDto>> getFeedback(@PathVariable Long answerId) {
        FeedbackResponseDto responseDto = aiFeedbackService.getFeedback(answerId);
        return ResponseEntity.ok(CustomResponse.ok(responseDto));
    }
}
