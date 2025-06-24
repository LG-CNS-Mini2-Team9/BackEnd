package com.team9.ai_feedback_service.global.exception;

public class AIFeedbackNotFoundException extends RuntimeException {

    public AIFeedbackNotFoundException(String message) {
        super(message);
    }

    public static AIFeedbackNotFoundException forAnswerId(Long answerId) {
        return new AIFeedbackNotFoundException("AnswerId가 " + answerId + "인 AI 피드백을 찾을 수 없습니다.");
    }
}
