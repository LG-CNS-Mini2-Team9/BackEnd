package com.lgcns.backend.csquestion.scheduler;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

@Component
public class AIQuestionScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        System.out.println("[스케줄러 Bean 생성 확인] AIQuestionScheduler 빈 등록 완료");
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    // @Scheduled(cron = "*/30 * * * * *") // 매 30초마다 실행 (Test 코드)
    public void callGenerateQuestionAPI() {
        System.out.println("[스케줄러 동작 테스트] 호출 시작");

        try {
            String url = "http://localhost:8080/api/questions";
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            System.out.println("AI 질문 생성 성공: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("스케줄러 API 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
