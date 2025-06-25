package com.team9.ai_feedback_service.api.backend;

import com.team9.ai_feedback_service.domain.dto.response.FeedbackScoreResponseDto;
import com.team9.ai_feedback_service.domain.dto.response.QuestionResponseDto;
import com.team9.ai_feedback_service.domain.dto.request.QuestionRequestDto;
import com.team9.ai_feedback_service.global.common.response.CustomResponse;
import com.team9.ai_feedback_service.service.AIQuestionService;
import com.team9.common.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping(value = "/internal/ai/questions", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class BackendAIQuestionController {

    private final AIQuestionService aiQuestionService;

    @PostMapping
    public ResponseEntity<CustomResponse<QuestionResponseDto>> generateCSQuestion(@RequestBody QuestionRequestDto request) {

        QuestionResponseDto responseDto = aiQuestionService.generateCSQuestion(request.getCategory(), request.getExistingKeywords());

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(responseDto));
    }
}