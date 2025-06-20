package com.lgcns.backend.unit.aiQuestion.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;

import com.lgcns.backend.ai.dto.AIResponse;
import com.lgcns.backend.csquestion.dto.CSQuestionResponse;
import com.lgcns.backend.csquestion.repository.CSQuestionRepository;
import com.lgcns.backend.csquestion.service.AIQuestionService;
import com.lgcns.backend.global.domain.Category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class AIQuestionServiceTest {

    @MockBean
    private CSQuestionRepository csQuestionRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private AIQuestionService aiQuestionService;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @BeforeEach
    void setup() throws Exception {
        java.lang.reflect.Field fieldUrl = AIQuestionService.class.getDeclaredField("API_URL");
        fieldUrl.setAccessible(true);
        fieldUrl.set(aiQuestionService, API_URL);

        java.lang.reflect.Field fieldKey = AIQuestionService.class.getDeclaredField("API_KEY");
        fieldKey.setAccessible(true);
        fieldKey.set(aiQuestionService, "dummy-api-key");
    }

    @DisplayName("AIQuestion - 텍스트 섞인 JSON 응답의 경우, 파싱 정상 동작 테스트")
    @Test
    void createAIQuestion_shouldParseValidJsonFromMixedResponse() {
        // AI가 특수문자와 텍스트가 섞인 JSON 응답을 반환했다고 가정
        String aiRawResponse = "Some random text ### {\"category\":\"네트워크\",\"content\":\"질문 내용\",\"keyword\":\"지연\"} ### more text";

        // AIResponse, Choice, Message 객체 Mock 세팅
        AIResponse response = new AIResponse();
        AIResponse.Choice choice = new AIResponse.Choice();
        AIResponse.Message message = new AIResponse.Message();

        message.setContent(aiRawResponse);
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        given(restTemplate.postForObject(
                org.mockito.ArgumentMatchers.eq(API_URL),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                org.mockito.ArgumentMatchers.eq(AIResponse.class)
        )).willReturn(response);

        given(csQuestionRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0)); // save 호출 시 저장 객체 그대로 리턴

        CSQuestionResponse.AIQuestionResponse result = aiQuestionService.createAIQuestion();

        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo(Category.네트워크);
        assertThat(result.getContent()).isEqualTo("질문 내용");
        assertThat(result.getKeyword()).isEqualTo("지연");
    }

    @DisplayName("AIQuestion - 응답에 category 누락되었을 경우, 오류 테스트")
    @Test
    void createAIQuestion_shouldThrowException_whenMissingCategory() {
        String aiRawResponse = "{\"content\":\"질문 내용\",\"keyword\":\"지연\"}";

        AIResponse response = new AIResponse();
        AIResponse.Choice choice = new AIResponse.Choice();
        AIResponse.Message message = new AIResponse.Message();

        message.setContent(aiRawResponse);
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        given(restTemplate.postForObject(
                org.mockito.ArgumentMatchers.eq(API_URL),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                org.mockito.ArgumentMatchers.eq(AIResponse.class)
        )).willReturn(response);

        assertThatThrownBy(() -> aiQuestionService.createAIQuestion())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("카테고리 정보가 없습니다.");
    }

    @DisplayName("AIQuestion - 응답에 content 누락되었을 경우, 오류 테스트")
    @Test
    void createAIQuestion_shouldThrowException_whenMissingContent() {
        String aiRawResponse = "{\"category\":\"네트워크\",\"keyword\":\"지연\"}"; // content 누락

        AIResponse response = new AIResponse();
        AIResponse.Choice choice = new AIResponse.Choice();
        AIResponse.Message message = new AIResponse.Message();

        message.setContent(aiRawResponse);
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        given(restTemplate.postForObject(
                org.mockito.ArgumentMatchers.eq(API_URL),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                org.mockito.ArgumentMatchers.eq(AIResponse.class)
        )).willReturn(response);

        assertThatThrownBy(() -> aiQuestionService.createAIQuestion())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("질문 내용이 없습니다.");
    }

    @DisplayName("AIQuestion - 응답에 keyword 누락되었을 경우, 오류 테스트")
    @Test
    void createAIQuestion_shouldThrowException_whenMissingKeyword() {
        String aiRawResponse = "{\"category\":\"네트워크\",\"content\":\"질문 내용\"}"; // keyword 누락

        AIResponse response = new AIResponse();
        AIResponse.Choice choice = new AIResponse.Choice();
        AIResponse.Message message = new AIResponse.Message();

        message.setContent(aiRawResponse);
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        given(restTemplate.postForObject(
                org.mockito.ArgumentMatchers.eq(API_URL),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                org.mockito.ArgumentMatchers.eq(AIResponse.class)
        )).willReturn(response);

        assertThatThrownBy(() -> aiQuestionService.createAIQuestion())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("핵심 키워드가 없습니다.");
    }

    @DisplayName("AIQuestion - 응답에 JSON 형식이 없을 경우, 오류 테스트")
    @Test
    void createAIQuestion_shouldThrowException_whenNoJsonFormatInResponse() {
        // AI 응답에 JSON 형식이 없는 경우 (중괄호가 없어서 파싱 불가)
        String aiRawResponse = "이 응답에는 JSON 형식이 포함되어 있지 않습니다.";

        AIResponse response = new AIResponse();
        AIResponse.Choice choice = new AIResponse.Choice();
        AIResponse.Message message = new AIResponse.Message();

        message.setContent(aiRawResponse);
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        given(restTemplate.postForObject(
                org.mockito.ArgumentMatchers.eq(API_URL),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                org.mockito.ArgumentMatchers.eq(AIResponse.class)
        )).willReturn(response);

        assertThatThrownBy(() -> aiQuestionService.createAIQuestion())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("AI 응답에 JSON 형식이 없습니다");
    }
}
