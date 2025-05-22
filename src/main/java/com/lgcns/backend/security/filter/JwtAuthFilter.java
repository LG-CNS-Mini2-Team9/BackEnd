package com.lgcns.backend.security.filter;

import java.io.IOException;
import java.util.List;

import com.lgcns.backend.global.code.GeneralErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import com.lgcns.backend.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
    JwtUtil jwtUtil;
    UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // 직접 호출하는 놈이 아니라, OncePerRequestFilter가 자동으로 호출 해줌
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
    
        // ✅ 토큰이 아예 없으면 그냥 통과 (여기 없으면 문제 생김)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
    
        try {
            String token = authHeader.substring(7);
    
            if (jwtUtil.validateToken(token)) {
                String userEmail = jwtUtil.extractEmailFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
    
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
    
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new JwtException("Invalid token");
            }
    
        } catch (ExpiredJwtException e) {
            // 토큰 만료 로그만 남기고 통과
            logger.warn("Expired JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        
        } catch (JwtException | IllegalArgumentException e) {
            // 잘못된 토큰도 마찬가지로 처리
            logger.warn("Invalid JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    
        filterChain.doFilter(request, response);
    }
}

