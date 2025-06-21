package com.team9.api_gateway.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDto {
    @NotBlank(message = "리프레시 토큰을 입력하세요.")
    private String token;
}
