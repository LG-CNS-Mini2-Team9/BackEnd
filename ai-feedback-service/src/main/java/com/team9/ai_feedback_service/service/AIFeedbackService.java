package com.team9.ai_feedback_service.service;

import com.team9.ai_feedback_service.domain.AIFeedback;
import com.team9.ai_feedback_service.domain.dto.response.FeedbackScoreResponseDto;
import com.team9.ai_feedback_service.domain.repository.AIFeedbackRepository;
import com.team9.ai_feedback_service.global.exception.AIFeedbackNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AIFeedbackService {

    private final AIFeedbackRepository aiFeedbackRepository;


    public FeedbackScoreResponseDto getFeedbackScore(Long answerId) {
        AIFeedback aiFeedback = aiFeedbackRepository.findByAnswerId(answerId)
                .orElseThrow(() -> AIFeedbackNotFoundException.forAnswerId(answerId));
        return new FeedbackScoreResponseDto(answerId, aiFeedback.getScore());
    }

}
