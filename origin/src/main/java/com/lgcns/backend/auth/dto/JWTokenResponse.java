package com.lgcns.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTokenResponse {
    private String accessToken;
    private String refreshToken;
}
