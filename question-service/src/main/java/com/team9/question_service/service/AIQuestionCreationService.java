package com.team9.question_service.service;

import com.team9.question_service.domain.Question;
import com.team9.question_service.dto.AiCreatedQuestionDto;
import com.team9.question_service.dto.AiGenerationRequestDto;
import com.team9.question_service.global.domain.Category;
import com.team9.question_service.repository.QuestionRepository;
import com.team9.question_service.remote.AIFeedbackServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIQuestionCreationService {

    private final QuestionRepository questionRepository;
    private final AIFeedbackServiceClient aiFeedbackServiceClient;
    // 요청 시 사용할 카테고리 목록과 랜덤 객체를 미리 정의합니다.
    private static final List<Category> CATEGORIES = List.of(Category.values());
    private static final Random RANDOM = new Random();

    /**
     * AI를 통해 새로운 질문을 하나 생성하고 DB에 저장합니다.
     */
    @Transactional
    public void createAiQuestion() {
        // 1. DB에서 기존 키워드 목록을 조회하여 중복을 방지합니다.
        List<String> existingKeywords = questionRepository.findAll().stream()
                .map(Question::getKeyword)
                .filter(Objects::nonNull) // keyword가 null인 경우는 제외합니다.
                .collect(Collectors.toList());

        // 2. ai-feedback-service API의 요구사항에 따라 랜덤 카테고리를 선택합니다.
        Category randomCategory = CATEGORIES.get(RANDOM.nextInt(CATEGORIES.size()));

        log.info("기존 키워드 {}개를 {} 카테고리로 ai-feedback-service에 전달합니다.", existingKeywords.size(), randomCategory.name());

        // 3. Feign Client를 통해 ai-feedback-service에 질문 생성을 요청합니다.
        AiGenerationRequestDto request = new AiGenerationRequestDto(randomCategory.name(), existingKeywords);
        AiCreatedQuestionDto newQuestionDto = aiFeedbackServiceClient.generateQuestion(request).getResult();

        // 4. 받은 결과로 Question 엔티티를 생성하고 DB에 저장합니다.
        Question newQuestion = Question.builder()
                .category(Category.valueOf(newQuestionDto.getCategory().toUpperCase()))
                .content(newQuestionDto.getContent())
                .keyword(newQuestionDto.getKeyword())
                .hint(newQuestionDto.getHint())
                .build();

        Question savedQuestion = questionRepository.save(newQuestion);
        log.info("새로운 AI 질문을 저장했습니다. ID: {}", savedQuestion.getId());
    }
}