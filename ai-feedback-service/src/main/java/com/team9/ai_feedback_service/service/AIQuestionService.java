package com.team9.ai_feedback_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.team9.ai_feedback_service.config.GeminiClient;
import com.team9.ai_feedback_service.domain.dto.response.QuestionResponseDto;
import com.team9.ai_feedback_service.global.exception.AIQuestionGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIQuestionService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionResponseDto generateCSQuestion(String category, List<String> existingKeywords) {

        try {
            Schema questionSchema = createCSQuestionSchema();

            GenerateContentConfig genConfig = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .responseSchema(questionSchema)
                    .candidateCount(1)
                    .maxOutputTokens(600)
                    .temperature(0.7F)
                    .build();

            String prompt = createDetailedPromptWithKeywordExclusion(category, existingKeywords);

            GenerateContentResponse response = geminiClient.client.models.generateContent(
                    geminiClient.getModel(),
                    prompt,
                    genConfig
            );

            String result = response.text();

            if (result == null || result.trim().isEmpty()) {
                throw AIQuestionGenerationException.invalidResponse();
            }

            log.info("\n=======질문 생성=======\n" + result);

            QuestionResponseDto questionResponseDto = objectMapper.readValue(result, QuestionResponseDto.class);

            return questionResponseDto;

        } catch (JsonProcessingException e) {
            throw AIQuestionGenerationException.invalidResponse();

        } catch (Exception e) {
            throw AIQuestionGenerationException.apiCallFailed(e.getMessage());
        }

    }

    private String createDetailedPromptWithKeywordExclusion(String category, List<String> existingKeywords) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("다음 조건에 따라 CS 면접 질문을 생성해주세요:\n\n");

        prompt.append("**카테고리**: ").append(category).append("\n\n");

        if (existingKeywords != null && !existingKeywords.isEmpty()) {
            prompt.append("**이미 사용된 키워드**: ");
            prompt.append(String.join(", ", existingKeywords));
            prompt.append("\n\n");
        }

        prompt.append("**요구사항**:\n");
        prompt.append("1. 위 카테고리에 관련된 CS 면접 질문 1개를 생성해주세요\n");
        prompt.append("2. 해당 질문에 대한 힌트를 한글로 제공해주세요 (답변에 포함되어야 할 핵심 단어들을 쉼표로 구분)\n");
        prompt.append("3. 질문과 밀접하게 관련된 키워드 1개를 생성해주세요\n\n");

        prompt.append("**중요한 제약사항**:\n");
        prompt.append("- 키워드는 '주제 + 핵심 개념' 형태로 작성해주세요. 예: '해시 테이블의 개념', '해시테이블의 충돌 해결'\n");
        prompt.append("- 쉼표로 구분된 여러 키워드는 절대 금지입니다\n");

        if (existingKeywords != null && !existingKeywords.isEmpty()) {
            prompt.append("- 이미 사용된 키워드와 유사하거나 동일한 키워드 사용 금지입니다\n");
        }

        prompt.append("- 실무 면접에서 나올 수 있는 중급 수준의 질문이어야 합니다\n");
        prompt.append("- 텍스트로 나타나지 않은 형태에 대한 질문은 금지입니다.\n");
        prompt.append("- 띄어쓰기 형식을 꼭 지켜주세요.\n");

        prompt.append("**응답 형식**:\n");
        prompt.append("```\n");
        prompt.append("질문: [면접 질문]\n");
        prompt.append("힌트: [핵심 단어1, 핵심 단어2, 핵심 단어3, ...]\n");
        prompt.append("키워드: [단일 키워드]\n");
        prompt.append("```\n\n");

        prompt.append("**재확인사항**:\n");
        prompt.append("- 키워드가 정확히 하나인지 확인\n");
        prompt.append("- 기존 키워드와 중복되지 않는지 확인\n");
        prompt.append("- 응답 형식이 올바른지 확인");

        return prompt.toString();
    }

    private Schema createCSQuestionSchema() {

        return Schema.builder()
                .type("object")
                .properties(ImmutableMap.of(
                        "category", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("입력받은 카테고리 그대로 전달")
                                .build(),
                        "content", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("구체적이고 실용적인 면접 질문")
                                .build(),
                        "keyword", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("질문과 관련된 단일 핵심 키워드 (하나의 단어 또는 짧은 구절, 쉼표 금지)")
                                .build(),
                        "hint", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("답변에 나와야 하는 핵심 단어(쉼표로 구분된 여러 개의 단어)")
                                .build()))
                .build();
    }
}