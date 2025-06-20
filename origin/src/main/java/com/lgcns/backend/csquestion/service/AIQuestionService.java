package com.lgcns.backend.csquestion.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.backend.ai.dto.AIRequest;
import com.lgcns.backend.ai.dto.AIRequest.OpenAiRequest;
import com.lgcns.backend.ai.dto.AIResponse;
import com.lgcns.backend.csquestion.domain.CSQuestion;
import com.lgcns.backend.csquestion.dto.CSQuestionResponse;
import com.lgcns.backend.csquestion.repository.CSQuestionRepository;
import com.lgcns.backend.global.domain.Category;

@Service
public class AIQuestionService {

    @Autowired
    private CSQuestionRepository csQuestionRepository;

    @Autowired
    private RestTemplate restTemplate;

    // openAI API로 Question 생성
    @Value("https://api.openai.com/v1/chat/completions")
    String API_URL;
    @Value("${openai.api.key}")
    String API_KEY;

    public CSQuestionResponse.AIQuestionResponse createAIQuestion() {
        List<CSQuestion> existQuestions = csQuestionRepository.findAll();
        String existKeywords = existQuestions.stream()
                .map(CSQuestion::getKeyword)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String prompt = String.join("\n",
                "다음 조건에 따라 새로운 CS 질문을 하나 생성해줘:",
                "1. 질문에 대한 키워드는 반드시 '하나만' 있어야 하며, 절대 두 개 이상이면 안 돼.",
                "2. 키워드는 질문과 밀접하게 관련된 '단일 단어 또는 짧은 구절'이야. 쉼표(,)로 구분된 키워드는 무조건 금지야.",
                "3. 출력 형식은 정확히 다음과 같은 JSON이어야 해: { \"category\": \"\", \"content\": \"\", \"keyword\": \"\" }",
                "4. 출력은 오직 이 JSON 객체 한 개만 포함해야 해. 그 외의 텍스트나 JSON 코드블록(예: ```json, ```)도 출력하지 마.",
                "5. category는 다음 중 하나여야 해: 자료구조, 알고리즘, 컴퓨터구조, 운영체제, 네트워크, 데이터베이스, 보안, 기타.",
                "6. 아래는 이미 사용된 키워드 목록이야. 이 목록에 있는 키워드는 절대 사용하지 마:",
                existKeywords,
                "다시 말하지만: 반드시 새로운 질문, 하나의 키워드, 하나의 JSON 객체만 생성해. 여러 키워드나 여러 JSON은 금지.");

        AIRequest.OpenAiRequest request = AIRequest.OpenAiRequest.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .max_tokens(300)
                .messages(List.of(
                        Map.of("role", "system", "content",
                                prompt),
                        Map.of("role", "user", "content", existKeywords)))
                .build();
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");
        HttpEntity<OpenAiRequest> entity = new org.springframework.http.HttpEntity<>(request, headers);

        AIResponse response = restTemplate.postForObject(API_URL, entity, AIResponse.class);
        // AI 답변 파싱 필요 (category, content, keyword, 기타 기호)
        String newQ = response.getChoices().get(0).getMessage().getContent();
        System.out.println("GPT 응답 원문:\n" + newQ);
        int firstBraceIndex = newQ.indexOf("{");
        int lastBraceIndex = newQ.lastIndexOf("}");
        if (firstBraceIndex == -1 || lastBraceIndex == -1 || firstBraceIndex > lastBraceIndex) {
            throw new IllegalArgumentException("AI 응답에 JSON 형식이 없습니다: " + newQ);
        }
        newQ = newQ.substring(firstBraceIndex, lastBraceIndex + 1).trim();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(newQ);
        } catch (Exception e) {
            throw new IllegalArgumentException("AI 응답 JSON 파싱 실패: " + e.getMessage());
        }

        String categoryStr = Optional.ofNullable(rootNode.get("category"))
                .map(JsonNode::asText)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 정보가 없습니다."));

        String content = Optional.ofNullable(rootNode.get("content"))
                .map(JsonNode::asText)
                .orElseThrow(() -> new IllegalArgumentException("질문 내용이 없습니다."));

        String keyword = Optional.ofNullable(rootNode.get("keyword"))
                .map(JsonNode::asText)
                .orElseThrow(() -> new IllegalArgumentException("핵심 키워드가 없습니다."));

        Category category = Arrays.stream(Category.values())
                .filter(c -> c.name().equalsIgnoreCase(categoryStr))
                .findFirst()
                .orElse(Category.기타);

        CSQuestion newQuestion = CSQuestion.builder()
                .category(category)
                .content(content)
                .keyword(keyword)
                .createdAt(LocalDateTime.now())
                .build();

        csQuestionRepository.save(newQuestion);
        return CSQuestionResponse.AIQuestionResponse.builder()
                .category(newQuestion.getCategory())
                .content(newQuestion.getContent())
                .keyword(newQuestion.getKeyword())
                .createdAt(newQuestion.getCreatedAt())
                .build();
    }
}
