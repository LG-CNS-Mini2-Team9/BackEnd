package com.team9.api_gateway.security.jwt;

import com.team9.api_gateway.security.jwt.authentication.JwtAuthentication;
import com.team9.api_gateway.security.jwt.authentication.UserPrinciple;
import com.team9.api_gateway.security.jwt.props.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final JwtConfigProperties configProperties;

    private volatile SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configProperties.getSecretKey()));
                }
            }
        }

        return secretKey;
    }

    private Claims verifyAndGetClaims(String token) {
        Claims claims;

        try {
            claims = Jwts.parser().verifyWith(getSecretKey()).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }

    private List<GrantedAuthority> getGrantedAuthority(String role) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (role != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        return grantedAuthorities;
    }

    public JwtAuthentication validateToken(String token) {
        String userId = null;

        final Claims claims = this.verifyAndGetClaims(token);

        if (claims == null) {
            return null;
        }

        Date expiration = claims.getExpiration();

        if (expiration == null || expiration.before(new Date())) {
            return null;
        }

        String tokenType = claims.get("tokenType", String.class);
        if (!"access".equals(tokenType)) {
            return null;
        }

        userId = claims.get("userId", String.class);
        UserPrinciple principle = new UserPrinciple(userId);

        return new JwtAuthentication(principle, token, getGrantedAuthority("user"));
    }

    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(configProperties.getHeader());
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
