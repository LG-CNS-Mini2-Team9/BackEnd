package com.team9.api_gateway.auth.jwt;

import com.team9.api_gateway.security.jwt.props.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {
    private final JwtConfigProperties configProperties;

    private volatile SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    secretKey = Keys.hmacShaKeyFor(
                            Decoders.BASE64.decode(configProperties.getSecretKey()));
                }
            }
        }

        return secretKey;
    }

    private Claims verifyAndGetClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(getSecretKey())
                    .build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }

    public String validateJwtToken(String refreshToken) {
        final Claims claims = verifyAndGetClaims(refreshToken);
        if (claims == null) {
            return null;
        }

        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            return null;
        }

        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equals(tokenType)){
            return null;
        }

        return claims.get("userId", String.class);
    }
}
