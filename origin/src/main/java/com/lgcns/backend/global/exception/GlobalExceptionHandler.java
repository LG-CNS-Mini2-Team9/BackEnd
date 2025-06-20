package com.lgcns.backend.global.exception;

import com.lgcns.backend.global.code.GeneralErrorCode;
import com.lgcns.backend.global.response.CustomResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CustomResponse.fail(GeneralErrorCode._FORBIDDEN, e.getMessage()));
    }

}
