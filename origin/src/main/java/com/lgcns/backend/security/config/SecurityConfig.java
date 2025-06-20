package com.lgcns.backend.security.config;

import com.lgcns.backend.security.filter.JwtAuthFilter;
import com.lgcns.backend.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtUtil jwtUtil,
                                                   UserDetailsService userDetailsService) throws Exception {
        http
                // csrf 비활성화
                .csrf(csrf -> {
                    csrf.disable();
                })
                // 경로별 인가
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인 모든 권한 인가
                        .requestMatchers("/user/signup").permitAll()
                        .requestMatchers("/user/login").permitAll()
                        .requestMatchers("/api/questions").permitAll()
                        .requestMatchers("/profile").permitAll()
                        .requestMatchers("/questions/**").permitAll()


                        // 게시글,댓글 관련 GET 요청만 허용
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/comm").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/comm/{postId}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/comm/{postId}/comments").permitAll()

                        .anyRequest().authenticated())
                // JWT 커스텀 사용자 인증 필터 등록
                .addFilterBefore(
                        new JwtAuthFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                // cors 설정
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            // 서버 접근을 허용할 출처 목록, "*"로 설정시, 모든 origin 가능
                            config.setAllowedOrigins(List.of("http://124.111.48.143:5173", "http://localhost:5173","https://csrrr.netlify.app"));
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH","DELETE"));
                            config.setAllowCredentials(true);
                            config.setAllowedHeaders(List.of("*"));
                            return config;
                        }));
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.setContentType("application/json; charset=UTF-8");
//                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
//                        }));

        return http.build();
    }


    // 비밀번호 암호화 기능
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }


}
