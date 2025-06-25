package com.team9.ai_feedback_service.global.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team9.ai_feedback_service.global.exception.AIFeedbackNotFoundException;
import com.team9.ai_feedback_service.global.exception.AIQuestionGenerationException;
import com.team9.common.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponseDto<String>> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON 파싱 오류 발생 : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.createError("JSON_PARSE_ERROR",
                        "AI 응답 데이터 파싱에 실패했습니다. 다시 시도해주세요."));
    }

    @ExceptionHandler(AIQuestionGenerationException.class)
    public ResponseEntity<ApiResponseDto<String>> handleAIQuestionGenerationException(AIQuestionGenerationException e) {
        log.error("AI 질문 생성 예외 발생: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.createError("QUESTION_GENERATION_ERROR", e.getMessage()));
    }

    @ExceptionHandler(AIFeedbackNotFoundException.class)
    public ResponseEntity<String> handleAIFeedbackNotFound(AIFeedbackNotFoundException e) {
        log.warn("AI Feedback not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<String>> handleGeneralException(Exception e) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.createError("INTERNAL_SERVER_ERROR",
                        "예상치 못한 오류가 발생했습니다."));
    }
}
