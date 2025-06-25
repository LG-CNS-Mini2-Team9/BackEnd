package com.team9.question_service.service;

import com.team9.common.code.GeneralErrorCode;
import com.team9.common.domain.Category;
import com.team9.common.exception.CustomException;
import com.team9.common.response.CustomResponse;
import com.team9.question_service.domain.Question;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.dto.UserProfileResponseDto;
import com.team9.question_service.repository.QuestionRepository;
import com.team9.question_service.remote.AnswerServiceClient;
import com.team9.question_service.remote.UserServiceClient; // UserServiceClient 임포트
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
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerServiceClient answerServiceClient;
    private final UserServiceClient userServiceClient; // UserServiceClient 주입

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

    public QuestionResponse getQuestionDetail(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));

        final Set<Long> submittedQuestionsIds = getSubmittedQuestionIds(userId);
        boolean isSubmitted = submittedQuestionsIds.contains(question.getId());

        return QuestionResponse.from(question, isSubmitted);
    }

    /**
     * 사용자에게 개인화된 질문을 추천합니다.
     * 1. 로그인 사용자의 관심 카테고리 중 아직 풀지 않은 문제
     * 2. (Fallback 1) 전체 문제 중 아직 풀지 않은 문제
     * 3. (Fallback 2) 오늘의 질문
     * 4. (비로그인) 오늘의 질문
     *
     * @param userId 사용자 ID (nullable)
     * @return 추천된 질문 DTO
     */
    public QuestionResponse getRecommendedQuestion(Long userId) {
        // 비로그인 사용자는 오늘의 질문을 반환
        if (userId == null) {
            return getTodayQuestion(null);
        }

        // 1. 사용자가 이미 답변한 질문 ID 목록 조회
        final Set<Long> submittedIds = getSubmittedQuestionIds(userId);
        // 네이티브 쿼리의 NOT IN 절에 빈 리스트가 들어가면 오류가 발생할 수 있으므로, 최소한의 원소를 넣어줍니다.
        final List<Long> excludedIds = submittedIds.isEmpty() ? List.of(0L) : List.copyOf(submittedIds);

        // 2. 사용자의 관심 카테고리 목록 조회
        List<String> interestCategories = getUserInterests(userId);

        Optional<Question> recommendedQuestion = Optional.empty();

        // 3. 관심 카테고리가 있는 경우, 해당 카테고리 내에서 랜덤 질문 조회
        if (interestCategories != null && !interestCategories.isEmpty()) {
            log.info("UserId: {}, 관심 카테고리 {} 내에서 미제출 문제 탐색", userId, interestCategories);
            recommendedQuestion = questionRepository.findRandomQuestionByCategoriesAndNotInIds(interestCategories, excludedIds);
        }

        // 4. Fallback 로직 1: 추천 질문이 없으면 (관심 카테고리가 없거나, 모두 풀었으면) 전체에서 랜덤 질문 조회
        if (recommendedQuestion.isEmpty()) {
            log.info("UserId: {}, 관심 카테고리 내 추천 문제 없음. 전체 문제에서 미제출 문제 탐색", userId);
            recommendedQuestion = questionRepository.findRandomQuestionNotInIds(excludedIds);
        }

        // 5. 최종 Fallback 로직 2: 그래도 없으면 (모든 문제를 다 풀었으면) 오늘의 질문 반환
        Question question = recommendedQuestion.orElseGet(this::findTodayQuestionEntity);

        // 추천 로직상 isSubmitted는 항상 false이지만, 명확성을 위해 한 번 더 확인
        boolean isSubmitted = submittedIds.contains(question.getId());
        return QuestionResponse.from(question, isSubmitted);
    }

    public QuestionResponse getTodayQuestion(Long userId) {
        Question question = findTodayQuestionEntity();
        final Set<Long> submittedIds = getSubmittedQuestionIds(userId);
        boolean isSubmitted = submittedIds.contains(question.getId());
        return QuestionResponse.from(question, isSubmitted);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));
        question.deactivate();
    }

    // 오늘의 질문 조회 로직을 별도 private 메서드로 분리하여 재사용성 높임
    private Question findTodayQuestionEntity() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Question question = questionRepository.findFirstByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay);

        if (question == null) {
            // 오늘의 질문이 없는 비상 상황을 위한 Fallback
            log.warn("오늘의 질문이 없습니다. 전체 질문 중 랜덤으로 하나를 반환합니다.");
            return questionRepository.findRandomQuestionNotInIds(List.of(0L))
                    .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));
        }
        return question;
    }

    // user-service 호출 및 예외 처리 로직
    private List<String> getUserInterests(Long userId) {
        try {
            // 1. 변경된 Feign Client 메서드를 호출합니다.
            ResponseEntity<CustomResponse<UserProfileResponseDto>> responseEntity = userServiceClient.getUserProfile(userId);

            // 2. 응답이 성공적인지, body가 null이 아닌지, isSuccess가 true인지 확인합니다.
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null && responseEntity.getBody().isSuccess()) {
                UserProfileResponseDto userProfile = responseEntity.getBody().getResult();

                // 3. 응답 DTO에서 관심사 목록을 추출합니다.
                if (userProfile != null && userProfile.getInterestCategories() != null) {
                    List<String> interests = userProfile.getInterestCategories();
                    log.info("user-service로부터 userId: {}의 관심 카테고리 {}개를 받았습니다.", userId, interests.size());
                    return interests;
                }
            } else {
                log.warn("user-service로부터 사용자 프로필을 가져오는 데 실패했습니다. status: {}, body: {}",
                        responseEntity.getStatusCode(), responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("user-service 호출 중 에러 발생: userId={}", userId, e);
        }
        // 실패 시 빈 리스트를 반환하여 NullPointerException을 방지합니다.
        return Collections.emptyList();
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