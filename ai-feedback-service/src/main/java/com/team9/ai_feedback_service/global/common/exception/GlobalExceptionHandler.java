package com.team9.ai_feedback_service.global.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team9.ai_feedback_service.global.common.code.GeneralErrorCode;
import com.team9.ai_feedback_service.global.common.response.CustomResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<CustomResponse<?>> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON 파싱 오류 발생 : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "AI 응답 데이터 파싱에 실패했습니다. 다시 시도해주세요."));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse<?>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(CustomResponse.fail(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomResponse.fail(GeneralErrorCode._NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<?>> handleGenericException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomResponse.fail(GeneralErrorCode._INTERNAL_SERVER_ERROR, null));
    }

}
