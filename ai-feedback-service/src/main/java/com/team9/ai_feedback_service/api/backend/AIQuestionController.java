package com.team9.ai_feedback_service.api.backend;

import com.team9.ai_feedback_service.domain.dto.response.QuestionResponseDto;
import com.team9.ai_feedback_service.remote.dto.request.QuestionRequestDto;
import com.team9.ai_feedback_service.service.AIQuestionService;
import com.team9.common.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping(value = "/backend/ai", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class AIQuestionController {

    private final AIQuestionService aiQuestionService;

    @PostMapping("/questions")
    public ApiResponseDto<QuestionResponseDto> generateCSQuestion(@RequestBody QuestionRequestDto request) {

        QuestionResponseDto responseDto = aiQuestionService.generateCSQuestion(request.getCategory(), request.getExistingKeywords());

        return ApiResponseDto.createOk(responseDto);
    }
}