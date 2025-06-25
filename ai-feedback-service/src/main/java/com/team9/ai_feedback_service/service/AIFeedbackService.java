package com.team9.ai_feedback_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.team9.ai_feedback_service.config.GeminiClient;
import com.team9.ai_feedback_service.domain.AIFeedback;
import com.team9.ai_feedback_service.domain.dto.request.FeedbackRequestDto;
import com.team9.ai_feedback_service.domain.dto.response.AIFeedbackResponseDto;
import com.team9.ai_feedback_service.domain.dto.response.FeedbackResponseDto;
import com.team9.ai_feedback_service.domain.dto.response.FeedbackScoreResponseDto;
import com.team9.ai_feedback_service.domain.repository.AIFeedbackRepository;
//import com.team9.ai_feedback_service.remote.question.RemoteQuestionService;
import com.team9.ai_feedback_service.global.common.code.AIFeedbackErrorCode;
import com.team9.ai_feedback_service.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AIFeedbackService {

    private final GeminiClient geminiClient;
    private final AIFeedbackRepository aiFeedbackRepository;
    //private final RemoteQuestionService remoteQuestionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public FeedbackScoreResponseDto createFeedback(FeedbackRequestDto feedbackRequestDto) {

// TODO: OPENFEIGN 확인하기
//        String question = remoteQuestionService.getQuestionDetail(feedbackRequestDto.getQuestionId(), null)
//                .getResult().getContent();

        String question = "자료 구조 스택에 대해 설명해주세요";

        try {
            Schema questionSchema = createFeedbackSchema();

            GenerateContentConfig genConfig = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .responseSchema(questionSchema)
                    .candidateCount(1)
                    .maxOutputTokens(600)
                    .temperature(0.7F)
                    .build();

            String prompt = createCSGradingPrompt(question, feedbackRequestDto.getAnswer());

            GenerateContentResponse response = geminiClient.client.models.generateContent(
                    geminiClient.getModel(),
                    prompt,
                    genConfig
            );

            String result = response.text();

            if (result == null || result.trim().isEmpty()) {
                throw CustomException.of(AIFeedbackErrorCode.AI_FEEDBACK_INVALID_RESPONSE);
            }

            log.info("\n=======피드백 생성=======\n" + result);

            AIFeedbackResponseDto aiResponse = objectMapper.readValue(result, AIFeedbackResponseDto.class);

            Optional<AIFeedback> existingFeedback = aiFeedbackRepository.findByAnswerId(feedbackRequestDto.getAnswerId());

            AIFeedback aiFeedback;

            if (existingFeedback.isPresent()) {
                aiFeedback = existingFeedback.get();
                aiFeedback.update(aiResponse.getScore(), aiResponse.getFeedback());
                log.info("기존 피드백 업데이트 - answerId: {}", feedbackRequestDto.getAnswerId());
            } else {
                aiFeedback = AIFeedback.of(feedbackRequestDto.getAnswerId(),
                        aiResponse.getScore(),
                        aiResponse.getFeedback());
                log.info("새로운 피드백 생성 - answerId: {}", feedbackRequestDto.getAnswerId());
                aiFeedbackRepository.save(aiFeedback);
            }

            return FeedbackScoreResponseDto.of(aiFeedback.getAnswerId(), aiFeedback.getScore());

        } catch (JsonProcessingException e) {
            throw CustomException.of(AIFeedbackErrorCode.AI_FEEDBACK_INVALID_RESPONSE);

        } catch (Exception e) {
            throw CustomException.of(AIFeedbackErrorCode.AI_FEEDBACK_API_FAILED);
        }
    }

    public FeedbackScoreResponseDto getFeedbackScore(Long answerId) {
        AIFeedback aiFeedback = aiFeedbackRepository.findByAnswerId(answerId)
                .orElseThrow(() -> CustomException.of(AIFeedbackErrorCode.AI_FEEDBACK_NOT_FOUND));
        return FeedbackScoreResponseDto.of(answerId, aiFeedback.getScore());
    }

    public FeedbackResponseDto getFeedback(Long answerId) {
        AIFeedback aiFeedback = aiFeedbackRepository.findByAnswerId(answerId)
                .orElseThrow(() -> CustomException.of(AIFeedbackErrorCode.AI_FEEDBACK_NOT_FOUND));
        return FeedbackResponseDto.of(aiFeedback);
    }

    private String createCSGradingPrompt(String csQuestion, String studentAnswer) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 컴퓨터 과학 전문가이자 교육자입니다. 주어진 CS 질문에 대한 학생의 답변을 채점하고 건설적인 피드백을 제공해주세요.\n\n");

        prompt.append("**채점 기준 (총 100점)**:\n");
        prompt.append("1. **정확성 (40점)**: 답변의 기술적 정확성과 올바른 개념 이해\n");
        prompt.append("2. **완성도 (25점)**: 질문에서 요구하는 모든 요소를 다뤘는지\n");
        prompt.append("3. **논리성 (20점)**: 답변의 논리적 구조와 설명의 일관성\n");
        prompt.append("4. **명확성 (15점)**: 설명의 명확함과 이해하기 쉬운 표현\n\n");

        prompt.append("**세부 채점 기준**:\n\n");

        // 정확성 기준
        prompt.append("**정확성 (40점)**:\n");
        prompt.append("- 36-40점: 완전히 정확하고 깊이 있는 이해\n");
        prompt.append("- 31-35점: 대부분 정확하나 사소한 오류 있음\n");
        prompt.append("- 26-30점: 핵심 개념은 맞으나 일부 중요한 오류\n");
        prompt.append("- 21-25점: 기본 개념은 이해했으나 상당한 오류\n");
        prompt.append("- 16-20점: 부분적 이해, 많은 오류\n");
        prompt.append("- 0-15점: 심각한 오해 또는 완전히 틀림\n\n");

        // 완성도 기준
        prompt.append("**완성도 (25점)**:\n");
        prompt.append("- 23-25점: 모든 요구사항을 완벽히 다룸\n");
        prompt.append("- 20-22점: 대부분의 요구사항을 다룸\n");
        prompt.append("- 17-19점: 주요 요구사항은 다뤘으나 일부 누락\n");
        prompt.append("- 14-16점: 절반 정도의 요구사항만 다룸\n");
        prompt.append("- 11-13점: 기본적인 부분만 다룸\n");
        prompt.append("- 0-10점: 질문에 제대로 답하지 못함\n\n");

        // 논리성 기준
        prompt.append("**논리성 (20점)**:\n");
        prompt.append("- 18-20점: 완벽한 논리적 흐름과 구조\n");
        prompt.append("- 16-17점: 대체로 논리적이나 사소한 흐름 문제\n");
        prompt.append("- 14-15점: 기본적인 논리는 있으나 일부 혼란\n");
        prompt.append("- 12-13점: 논리적 연결이 약함\n");
        prompt.append("- 10-11점: 논리적 구조가 부족\n");
        prompt.append("- 0-9점: 논리적 흐름이 전혀 없음\n\n");

        // 명확성 기준
        prompt.append("**명확성 (15점)**:\n");
        prompt.append("- 14-15점: 매우 명확하고 이해하기 쉬움\n");
        prompt.append("- 12-13점: 대체로 명확함\n");
        prompt.append("- 10-11점: 보통 수준의 명확성\n");
        prompt.append("- 8-9점: 다소 불분명한 부분 있음\n");
        prompt.append("- 6-7점: 이해하기 어려운 설명\n");
        prompt.append("- 0-5점: 매우 불분명하거나 이해 불가\n\n");

        prompt.append("**출력 형식**:\n");
        prompt.append("```\n");
        prompt.append("각 항목별 점수:\n");
        prompt.append("- 정확성: X/40점 - [간단한 설명]\n");
        prompt.append("- 완성도: X/25점 - [간단한 설명]\n");
        prompt.append("- 논리성: X/20점 - [간단한 설명]\n");
        prompt.append("- 명확성: X/15점 - [간단한 설명]\n");
        prompt.append("\n");
        prompt.append("잘한 점:\n");
        prompt.append("1. [구체적인 잘한 점]\n");
        prompt.append("2. [구체적인 잘한 점]\n");
        prompt.append("3. [구체적인 잘한 점]\n");
        prompt.append("\n");
        prompt.append("개선할 점:\n");
        prompt.append("1. [구체적인 개선사항]\n");
        prompt.append("2. [구체적인 개선사항]\n");
        prompt.append("3. [구체적인 개선사항]\n");
        prompt.append("\n");
        prompt.append("추가 학습 권장사항:\n");
        prompt.append("- [구체적인 학습 주제 또는 자료]\n");
        prompt.append("```\n\n");

        prompt.append("**CS 질문**:\n");
        prompt.append(csQuestion).append("\n\n");

        prompt.append("**학생 답변**:\n");
        prompt.append(studentAnswer).append("\n\n");

        prompt.append("**중요한 채점 가이드라인**:\n");
        prompt.append("- 건설적이고 격려하는 톤으로 피드백을 작성해주세요\n");
        prompt.append("- 구체적인 예시나 개선 방향을 제시해주세요\n");
        prompt.append("- 학생의 현재 수준을 고려한 적절한 추가 학습 자료를 추천해주세요\n");
        prompt.append("- 부분 점수도 적극적으로 활용해주세요\n\n");

        prompt.append("위 기준에 따라 정확하게 채점하고 상세한 피드백을 제공해주세요.");

        return prompt.toString();
    }

    private Schema createFeedbackSchema() {

        return Schema.builder()
                .type("object")
                .properties(ImmutableMap.of(
                        "score", Schema.builder()
                                .type(Type.Known.INTEGER)
                                .description("기준에 따라 채첨한 답변 점수")
                                .build(),
                        "feedback", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("답변에 대한 피드백(잘한 점과 아쉬운 점)")
                                .build()))
                .build();
    }

}
