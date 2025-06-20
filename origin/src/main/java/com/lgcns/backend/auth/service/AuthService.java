package com.lgcns.backend.auth.service;

import com.lgcns.backend.global.code.GeneralSuccessCode;
import com.lgcns.backend.global.response.CustomResponse;
import com.lgcns.backend.security.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public CustomResponse reissueToken(String refreshToken) {
        jwtUtil.validateToken(refreshToken); // 리프레시 토큰 유효성 검사, 유효하지 않으면 예외를 controller에 던져, catch

        String email = jwtUtil.extractEmailFromToken(refreshToken);

        String storedRefreshToken =
                (String) redisTemplate
                        .opsForValue()
                        .get("RT:" + email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new JwtException("Refresh Token이 없거나 일치하지 않습니다.");
        }

        String newAccessToken = jwtUtil.createAccessToken(email);
        return CustomResponse.success(GeneralSuccessCode._OK, newAccessToken);
    }
}
