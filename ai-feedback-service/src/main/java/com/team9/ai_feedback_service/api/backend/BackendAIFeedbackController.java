package com.team9.ai_feedback_service.api.backend;

import com.team9.ai_feedback_service.domain.dto.request.FeedbackRequestDto;
import com.team9.ai_feedback_service.domain.dto.response.FeedbackScoreResponseDto;
import com.team9.ai_feedback_service.global.common.response.CustomResponse;
import com.team9.ai_feedback_service.service.AIFeedbackService;
import com.team9.common.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/internal/ai/feedbacks", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class BackendAIFeedbackController {

    private final AIFeedbackService aiFeedbackService;

    @PostMapping()
    public ResponseEntity<CustomResponse<FeedbackScoreResponseDto>> createFeedback(@RequestBody FeedbackRequestDto feedbackRequestDto) {

        FeedbackScoreResponseDto responseDto = aiFeedbackService.createFeedback(feedbackRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(responseDto));
    }

    @GetMapping("/{answerId}/score")
    public ResponseEntity<CustomResponse<FeedbackScoreResponseDto>> getFeedbackScore(@PathVariable Long answerId) {

        FeedbackScoreResponseDto responseDto = aiFeedbackService.getFeedbackScore(answerId);

        return ResponseEntity.ok(CustomResponse.ok(aiFeedbackService.getFeedbackScore(answerId)));
    }

}
