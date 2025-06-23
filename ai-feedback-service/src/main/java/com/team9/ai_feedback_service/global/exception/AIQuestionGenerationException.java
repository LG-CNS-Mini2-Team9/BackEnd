package com.team9.ai_feedback_service.global.exception;

public class AIQuestionGenerationException extends RuntimeException {

    public AIQuestionGenerationException(String message) {
        super(message);
    }

    public static AIQuestionGenerationException apiCallFailed(String reason) {
        return new AIQuestionGenerationException("AI API 호출에 실패했습니다: " + reason);
    }

    public static AIQuestionGenerationException networkError() {
        return new AIQuestionGenerationException("네트워크 연결에 실패했습니다. 인터넷 연결을 확인해주세요.");
    }

    public static AIQuestionGenerationException invalidResponse() {
        return new AIQuestionGenerationException("AI로부터 올바르지 않은 응답을 받았습니다.");
    }
}
