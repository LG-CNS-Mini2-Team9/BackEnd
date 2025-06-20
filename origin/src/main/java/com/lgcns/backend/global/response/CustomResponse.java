package com.lgcns.backend.global.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lgcns.backend.global.code.BaseCode;
import com.lgcns.backend.global.code.BaseErrorCode;
import com.lgcns.backend.global.code.GeneralSuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class CustomResponse<T> {

    @JsonProperty("isSuccess")
    private Boolean isSuccess;
    @JsonProperty("message")
    private String message;
    @JsonProperty("code")
    private String code;
    @JsonProperty("result")
    private T result;

    public static <T> CustomResponse<T> ok(T result) {
        return success(GeneralSuccessCode._OK, result);
    }

    public static <T> CustomResponse<T> created(T result) {
        return success(GeneralSuccessCode._CREATED, result);
    }

    public static <T> CustomResponse<T> success(BaseCode code, T result) {
        return of(true, code.getMessage(), code.getCode(), result);
    }

    public static <T> CustomResponse<T> fail(BaseErrorCode code, T result) {
        return of(false, code.getMessage(), code.getCode(), result);
    }

    private static <T> CustomResponse<T> of(boolean isSuccess, String message, String code, T result) {
        return new CustomResponse<>(isSuccess, message, code, result);
    }

    public CustomResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public CustomResponse<T> body(T result) {
        this.result = result;
        return this;
    }

    // json 객체는 lombok get 함수 작동 안해서 따로 정의
    public Boolean isSuccess() {
        return this.isSuccess;
    }

}