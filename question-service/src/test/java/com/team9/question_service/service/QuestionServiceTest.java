package com.team9.question_service.service;

import com.team9.question_service.global.response.CustomResponse;
import com.team9.question_service.domain.Question;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.repository.QuestionRepository;
import com.team9.question_service.remote.AnswerServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // Pageable import
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerServiceClient answerServiceClient;

    @Test
    @DisplayName("로그인한 유저가 질문 목록 조회 시, 제출한 문제의 isSubmitted는 true여야 한다")
    void getQuestionList_withSubmittedAnswers() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Question> questions = List.of(
                Question.builder().id(101L).content("질문1").build(),
                Question.builder().id(102L).content("질문2").build(),
                Question.builder().id(103L).content("질문3").build()
        );
        Page<Question> questionPage = new PageImpl<>(questions, pageable, questions.size());

        List<Long> submittedIds = List.of(101L, 103L);
        CustomResponse<List<Long>> apiResponse = CustomResponse.ok(submittedIds);
        ResponseEntity<CustomResponse<List<Long>>> responseEntity = ResponseEntity.ok(apiResponse);

        // any(Pageable.class)를 사용하여 페이징이 적용된 findAll 메서드를 정확히 지정합니다.
        when(questionRepository.findAll(any(Pageable.class))).thenReturn(questionPage);
        when(answerServiceClient.getSubmittedQuestionIdsByUserId(userId)).thenReturn(responseEntity);

        // when
        Page<QuestionResponse> result = questionService.getQuestionList(null, pageable, userId);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).isSubmitted()).isTrue();
        assertThat(result.getContent().get(1).isSubmitted()).isFalse();
        assertThat(result.getContent().get(2).isSubmitted()).isTrue();
    }

    @Test
    @DisplayName("비로그인 유저가 질문 목록 조회 시, 모든 문제의 isSubmitted는 false여야 한다")
    void getQuestionList_forGuestUser() {
        // given
        Long userId = null;
        Pageable pageable = PageRequest.of(0, 10);

        List<Question> questions = List.of(
                Question.builder().id(101L).content("질문1").build(),
                Question.builder().id(102L).content("질문2").build()
        );
        Page<Question> questionPage = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findAll(any(Pageable.class))).thenReturn(questionPage);

        // when
        Page<QuestionResponse> result = questionService.getQuestionList(null, pageable, userId);

        // then
        verify(answerServiceClient, never()).getSubmittedQuestionIdsByUserId(any());
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(q -> !q.isSubmitted());
    }

    @Test
    @DisplayName("answer-service 통신 실패 시, 에러 없이 isSubmitted가 모두 false로 처리되어야 한다")
    void getQuestionList_whenAnswerServiceFails() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Question> questions = List.of(
                Question.builder().id(101L).content("질문1").build(),
                Question.builder().id(102L).content("질문2").build()
        );
        Page<Question> questionPage = new PageImpl<>(questions, pageable, questions.size());

        when(answerServiceClient.getSubmittedQuestionIdsByUserId(userId)).thenThrow(new RuntimeException("Answer service is down"));
        when(questionRepository.findAll(any(Pageable.class))).thenReturn(questionPage);

        // when
        Page<QuestionResponse> result = questionService.getQuestionList(null, pageable, userId);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(q -> !q.isSubmitted());
    }
}