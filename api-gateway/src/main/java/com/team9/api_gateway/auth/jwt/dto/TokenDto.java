package com.team9.api_gateway.auth.jwt.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class JwtToken {
        private String token;
        private Integer expiresIn;
    }

    @Getter
    @RequiredArgsConstructor
    public static class AccessToken {
        private final JwtToken access;
    }

    @Getter
    @RequiredArgsConstructor
    public static class AccessRefreshToken {
        private final JwtToken access;
        private final JwtToken refresh;
    }
}