package com.team9.api_gateway.advice;

import com.team9.api_gateway.common.dto.ApiResponseDto;
import com.team9.api_gateway.common.exception.BadParameter;
import com.team9.api_gateway.common.exception.ClientError;
import com.team9.api_gateway.common.exception.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(1)
@RestControllerAdvice
public class ApiCommonAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFound.class})
    public ApiResponseDto<String> handleNotFound(NotFound e) {
        return ApiResponseDto.createError(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadParameter.class})
    public ApiResponseDto<String> handleBadParameter(BadParameter e) {
        return ApiResponseDto.createError(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ClientError.class})
    public ApiResponseDto<String> handleClientError(ClientError e) {
        return ApiResponseDto.createError(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ApiResponseDto<String> handleException(Exception e) {
        log.error(e.getMessage());
        return ApiResponseDto.createError("Server Error", e.getMessage());
    }
}
