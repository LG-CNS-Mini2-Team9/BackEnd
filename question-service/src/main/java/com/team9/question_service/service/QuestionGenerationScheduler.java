package com.team9.question_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionGenerationScheduler {

    private final AIQuestionCreationService aiQuestionCreationService;

    /**
     * 매일 자정(00:00)에 AI를 통해 새로운 CS 질문 10개를 생성합니다.
     * - cron = "초 분 시 일 월 요일"
     * - "0 0 0 * * *" = 매일 0시 0분 0초
     * - zone = "Asia/Seoul" : 한국 시간 기준으로 동작하도록 설정합니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void generateDailyAiQuestions() {
        log.info("===== AI 질문 생성 스케줄링 작업을 시작합니다. =====");
        int numberOfQuestionsToGenerate = 10; // 하루에 생성할 질문 개수

        for (int i = 0; i < numberOfQuestionsToGenerate; i++) {
            try {
                log.info("{}번째 AI 질문 생성을 시도합니다.", i + 1);
                aiQuestionCreationService.createAiQuestion();
                // AI API의 분당 요청 제한(Rate Limit)을 피하기 위해 약간의 딜레이를 줍니다.
                Thread.sleep(5000); // 5초 대기
            } catch (Exception e) {
                // 한 번의 실패가 전체 작업을 중단시키지 않도록 예외를 처리하고 로그를 남깁니다.
                log.error("{}번째 AI 질문 생성 중 오류가 발생했습니다. 다음 질문 생성으로 넘어갑니다.", i + 1, e);
            }
        }
        log.info("===== AI 질문 생성 스케줄링 작업을 완료했습니다. =====");
    }
}