package com.team9.question_service.service;

import com.team9.common.code.GeneralErrorCode;
import com.team9.common.domain.Category;
import com.team9.common.exception.CustomException;
import com.team9.question_service.domain.Question;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.repository.QuestionRepository;
// TODO: Feign Client import 추가
// import com.team9.question_service.feign.AnswerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor // @Autowired 대신 생성자 주입 사용 (권장)
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    // TODO: Answer 서비스와 통신하기 위한 Feign Client 주입
    // private final AnswerServiceClient answerServiceClient;

    public Page<QuestionResponse> getQuestionList(String categoryName, Pageable pageable, Long userId) {
        Page<Question> questions;

        // 원본의 CSAnswerRepository 직접 접근 로직을 Feign Client 호출로 대체해야 함
        // TODO: answer-service에 API를 요청하여 현재 사용자가 답변한 질문 ID 목록을 가져옵니다.
        // final Set<Long> submittedQuestionsIds;
        // if (userId != null) {
        //     // 예시: CustomResponse<List<Long>> response = answerServiceClient.getSubmittedQuestionIds(userId);
        //     // submittedQuestionsIds = new HashSet<>(response.getResult());
        // } else {
        //     submittedQuestionsIds = Collections.emptySet();
        // }
        final Set<Long> submittedQuestionsIds = new HashSet<>(); // 임시 코드

        if (categoryName == null) {
            questions = questionRepository.findAll(pageable);
        } else {
            try {
                // 원본과 달리, 잘못된 카테고리 입력 시 기본 목록을 보여주는 대신 명확한 에러를 반환
                Category category = Category.valueOf(categoryName.toUpperCase());
                questions = questionRepository.findByCategory(category, pageable);
            } catch (IllegalArgumentException e) {
                throw new CustomException(GeneralErrorCode._BAD_REQUEST);
            }
        }

        return questions.map(q -> QuestionResponse.builder()
                .id(q.getId())
                .category(q.getCategory())
                .createdAt(q.getCreatedAt())
                .content(q.getContent())
                .difficulty(q.getDifficulty())
                .hint(q.getHint())
                .isSubmitted(userId != null && submittedQuestionsIds.contains(q.getId()))
                .build());
    }

    public QuestionResponse getQuestionDetail(Long id) {
        Question question = questionRepository.findById(id)
                // 원본의 IllegalArgumentException 보다 구체적인 예외 사용
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));

        return QuestionResponse.builder()
                .id(question.getId())
                .category(question.getCategory())
                .createdAt(question.getCreatedAt())
                .content(question.getContent())
                .difficulty(question.getDifficulty())
                .hint(question.getHint())
                .isSubmitted(false) // 상세 조회는 사용자 정보가 없으므로 기본값 false
                .build();
    }

    public QuestionResponse getTodayQuestion(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Question question = questionRepository.findFirstByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay);

        if (question == null) {
            throw new NoSuchElementException("오늘의 질문이 아직 등록되지 않았습니다.");
        }

        // TODO: answer-service에 API를 요청하여 이 질문에 대한 사용자의 답변 여부를 확인해야 함
        // boolean isSubmitted = false;
        // if (userId != null) {
        //     // 예시: isSubmitted = answerServiceClient.isQuestionSubmitted(userId, question.getId()).getResult();
        // }
        boolean isSubmitted = false; // 임시 코드

        return QuestionResponse.builder()
                .id(question.getId())
                .category(question.getCategory())
                .createdAt(question.getCreatedAt())
                .content(question.getContent())
                .difficulty(question.getDifficulty())
                .hint(question.getHint())
                .isSubmitted(isSubmitted)
                .build();
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));

        // 원본의 물리적 삭제(repository.delete) 대신 논리적 삭제(상태 변경) 사용
        question.deactivate();
        // questionRepository.save(question); // @Transactional에 의해 변경 감지(Dirty Checking)되어 자동 저장됩니다.
    }
}