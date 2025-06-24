package com.lgcns.backend.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 256비트(32바이트) 이상의 키를 필요
    // Base64는 문자 1개가 6비트로 처리 (3바이트 == Base64 문자 4개)
    // 문자 4개당 3바이트를 표현하므로 32바이트를 넘기려면 최소 43자 이상 필요
    // (40개 == 30바이트, 43자 == 32.25바이트, 44자 == 33바이트)
    private final String SECRET = "1234567890123456789012345678901234567890123";
    private final long EXPIRATION = 1000 * 60 * 60; // 1시간
//    private final long EXPIRATION = 1000; // 1초, 실험용 코드
    private final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String email) { // JWT 생성
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME)) // 보통 1주일~2주
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmailFromToken(String token) { // 정보 추출
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

//    public boolean validateToken(String token) { // 토큰 유효성 검사
//        try {
//            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }

    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return true;
    }
}