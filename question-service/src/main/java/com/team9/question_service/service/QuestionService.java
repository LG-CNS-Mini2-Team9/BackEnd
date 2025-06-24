package com.team9.question_service.service;

import com.team9.common.code.GeneralErrorCode;
import com.team9.common.domain.Category;
import com.team9.common.exception.CustomException;
import com.team9.common.response.CustomResponse;
import com.team9.question_service.domain.Question;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.repository.QuestionRepository;
import com.team9.question_service.remote.AnswerServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerServiceClient answerServiceClient;

    public Page<QuestionResponse> getQuestionList(String categoryName, Pageable pageable, Long userId) {
        final Set<Long> submittedQuestionsIds = getSubmittedQuestionIds(userId);

        Page<Question> questions;
        if (categoryName == null || categoryName.isBlank()) {
            questions = questionRepository.findAll(pageable);
        } else {
            try {
                Category category = Category.valueOf(categoryName.toUpperCase());
                questions = questionRepository.findByCategory(category, pageable);
            } catch (IllegalArgumentException e) {
                throw new CustomException(GeneralErrorCode._BAD_REQUEST);
            }
        }

        return questions.map(q -> QuestionResponse.from(q, submittedQuestionsIds.contains(q.getId())));
    }

    // userId 파라미터 추가
    public QuestionResponse getQuestionDetail(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));

        // userId가 있을 경우 isSubmitted를 판단
        final Set<Long> submittedQuestionsIds = getSubmittedQuestionIds(userId);
        boolean isSubmitted = submittedQuestionsIds.contains(question.getId());

        return QuestionResponse.from(question, isSubmitted);
    }

    public QuestionResponse getTodayQuestion(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Question question = questionRepository.findFirstByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay);

        if (question == null) {
            throw new NoSuchElementException("오늘의 질문이 아직 등록되지 않았습니다.");
        }

        final Set<Long> submittedQuestionsIds = getSubmittedQuestionIds(userId);
        boolean isSubmitted = submittedQuestionsIds.contains(question.getId());

        return QuestionResponse.from(question, isSubmitted);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));
        question.deactivate();
    }

    private Set<Long> getSubmittedQuestionIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        try {
            ResponseEntity<CustomResponse<List<Long>>> responseEntity = answerServiceClient.getSubmittedQuestionIdsByUserId(userId);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null && responseEntity.getBody().isSuccess()) {
                List<Long> ids = responseEntity.getBody().getResult();
                if (ids != null) {
                    log.info("answer-service로부터 userId: {}의 제출된 문제 ID {}개를 받았습니다.", userId, ids.size());
                    return new HashSet<>(ids);
                }
            } else {
                log.warn("answer-service로부터 제출된 질문 ID 목록을 가져오는 데 실패했습니다. status: {}, body: {}",
                        responseEntity.getStatusCode(), responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("answer-service 호출 중 에러 발생: userId={}", userId, e);
        }

        return Collections.emptySet();
    }
}