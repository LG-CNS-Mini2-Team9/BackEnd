package com.team9.statistic_service.global.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /**
     * Feign 로깅 레벨 설정
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Feign 요청 인터셉터
     * 공통 헤더 설정 등을 할 수 있음
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Content-Type", "application/json");
            // 필요시 추가 헤더 설정
        };
    }
}