package com.team9.api_gateway.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor()
@Getter
public class ApiResponseDto<T> {
    private String code;
    private String message;
    private T data;

    private ApiResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private ApiResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDto<T> createOk(T data) {
        return new ApiResponseDto<>(
                "OK",
                "요청에 성공했습니다.",
               data
        );
    }

    public static ApiResponseDto<String> defaultOk() {
        return createOk(null);
    }

    public static ApiResponseDto<String> createError(String code, String message) {
        return new ApiResponseDto<>(code, message);
    }
}
