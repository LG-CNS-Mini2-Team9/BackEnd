package com.team9.statistic_service.global.exception;

import com.team9.statistic_service.global.code.GeneralErrorCode;
import com.team9.statistic_service.global.response.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse<Object>> handleCustomException(CustomException e) {
        log.error("CustomException occurred: {}", e.getMessage(), e);

        return ResponseEntity
                .status(e.getBaseErrorCode().getHttpStatus())
                .body(CustomResponse.fail(e.getBaseErrorCode()));
    }

    // Validation 에러 처리 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error occurred: {}", e.getMessage());

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);

        return ResponseEntity
                .badRequest()
                .body(CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, errorMessage));
    }

    // Bind 에러 처리
    @ExceptionHandler(BindException.class)
    public ResponseEntity<CustomResponse<Object>> handleBindException(BindException e) {
        log.error("Bind error occurred: {}", e.getMessage());

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);

        return ResponseEntity
                .badRequest()
                .body(CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, errorMessage));
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred: {}", e.getMessage());

        return ResponseEntity
                .badRequest()
                .body(CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, e.getMessage()));
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Object>> handleException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);

        return ResponseEntity
                .internalServerError()
                .body(CustomResponse.fail(GeneralErrorCode._INTERNAL_SERVER_ERROR));
    }
}
