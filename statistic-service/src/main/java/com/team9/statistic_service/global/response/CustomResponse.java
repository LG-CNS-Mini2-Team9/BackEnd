package com.team9.statistic_service.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team9.statistic_service.global.code.BaseErrorCode;
import com.team9.statistic_service.global.code.BaseSuccessCode;
import com.team9.statistic_service.global.code.GeneralSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse<T> {
    private Boolean success;
    private String code;
    private String message;
    private T data;

    // 성공 응답 생성 메서드들
    public static <T> CustomResponse<T> ok(T data) {
        return new CustomResponse<>(true, GeneralSuccessCode._OK.getCode(),
                GeneralSuccessCode._OK.getMessage(), data);
    }

    public static <T> CustomResponse<T> created(T data) {
        return new CustomResponse<>(true, GeneralSuccessCode._CREATED.getCode(),
                GeneralSuccessCode._CREATED.getMessage(), data);
    }

    public static <T> CustomResponse<T> success(BaseSuccessCode successCode, T data) {
        return new CustomResponse<>(true, successCode.getCode(),
                successCode.getMessage(), data);
    }

    // 실패 응답 생성 메서드들
    public static <T> CustomResponse<T> fail(BaseErrorCode errorCode) {
        return new CustomResponse<>(false, errorCode.getCode(),
                errorCode.getMessage(), null);
    }

    public static <T> CustomResponse<T> fail(BaseErrorCode errorCode, String message) {
        return new CustomResponse<>(false, errorCode.getCode(), message, null);
    }

    public static <T> CustomResponse<T> fail(BaseErrorCode errorCode, T data) {
        return new CustomResponse<>(false, errorCode.getCode(),
                errorCode.getMessage(), data);
    }
}
