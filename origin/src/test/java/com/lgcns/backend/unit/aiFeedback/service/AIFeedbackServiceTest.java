package com.lgcns.backend.unit.aiFeedback.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lgcns.backend.ai.dto.AIResponse;
import com.lgcns.backend.ai.dto.AIResponse.Choice;
import com.lgcns.backend.ai.dto.AIResponse.Message;
import com.lgcns.backend.csanswer.domain.CSAnswer;
import com.lgcns.backend.csanswer.dto.CSAnswerResponse;
import com.lgcns.backend.csanswer.repository.CSAnswerRepository;
import com.lgcns.backend.csanswer.service.AIFeedbackService;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class AIFeedbackServiceTest {

    @Autowired
    private AIFeedbackService aiFeedbackService;

    @MockBean
    private CSAnswerRepository csAnswerRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private UserDetails userDetails;

    private User mockUser;
    private CSAnswer mockAnswer;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setNickname("tester");

        // 답변 생성
        mockAnswer = new CSAnswer();
        mockAnswer.setId(10L);
        mockAnswer.setContent("이것은 CS 질문에 대한 답변입니다.");
        mockAnswer.setUser(mockUser);

        given(userDetails.getUsername()).willReturn(mockUser.getEmail());
    }

    @DisplayName("AIFeedback - Success")
    @Test
    void createAIFeedback_success() {
        // given
        given(userRepository.findByEmail(mockUser.getEmail()))
                .willReturn(Optional.of(mockUser));
        given(csAnswerRepository.findById(mockAnswer.getId()))
                .willReturn(Optional.of(mockAnswer));

        // AIResponse 구성
        Message message = new Message();
        message.setContent("좋은 피드백입니다.");

        Choice choice = new Choice();
        choice.setMessage(message);

        List<Choice> choiceList = new ArrayList<>();
        choiceList.add(choice);

        AIResponse mockResponse = new AIResponse();
        mockResponse.setChoices(choiceList);

        given(restTemplate.postForObject(
                org.mockito.ArgumentMatchers.eq("https://api.openai.com/v1/chat/completions"),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                org.mockito.ArgumentMatchers.eq(AIResponse.class)
        )).willReturn(mockResponse);

        // when
        CSAnswerResponse.AIFeedbackResponse response =
                aiFeedbackService.createAIFeedback(mockAnswer.getId(), userDetails);

        // then
        assertThat(response.getCsanswer_id()).isEqualTo(mockAnswer.getId());
        assertThat(response.getCsanswer_feedback()).contains("피드백");
    }

    @DisplayName("AIFeedback - 존재하지 않는 answerId 사용 시, 오류 테스트")
    @Test
    void createAIFeedback_invalidAnswer_throwsException() {
        given(userRepository.findByEmail(mockUser.getEmail()))
                .willReturn(Optional.of(mockUser));
        given(csAnswerRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                aiFeedbackService.createAIFeedback(999L, userDetails));
    }

    @DisplayName("AIFeedback - 자신의 answerId가 아닌 경우, 오류 테스트")
    @Test
    void createAIFeedback_userMismatch_throwsException() {
        // 다른 사용자 생성
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        // 다른 사용자의 답변 생성
        CSAnswer otherAnswer = new CSAnswer();
        otherAnswer.setId(11L);
        otherAnswer.setContent("다른 사용자의 답변입니다.");
        otherAnswer.setUser(otherUser);

        given(userRepository.findByEmail(mockUser.getEmail()))
                .willReturn(Optional.of(mockUser));
        given(csAnswerRepository.findById(otherAnswer.getId()))
                .willReturn(Optional.of(otherAnswer));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                aiFeedbackService.createAIFeedback(otherAnswer.getId(), userDetails));

        assertThat(ex.getMessage()).contains("자신의 답변만 피드백 요청");
    }
}