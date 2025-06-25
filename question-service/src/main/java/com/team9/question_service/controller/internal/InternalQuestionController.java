package com.team9.question_service.controller.internal;

import com.team9.question_service.global.response.CustomResponse;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.service.AIQuestionCreationService;
import com.team9.question_service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/questions")
public class InternalQuestionController {
    private final QuestionService questionService;
    private final AIQuestionCreationService aiQuestionCreationService;

    @GetMapping("/{questionId}")
    public ResponseEntity<CustomResponse<QuestionResponse>> getQuestionById(
            @PathVariable("questionId") Long questionId) {
        QuestionResponse question = questionService.getQuestionDetail(questionId, null);
        return ResponseEntity.ok(CustomResponse.ok(question));
    }

    /**
     * 개발자/관리자용: AI를 통해 수동으로 새로운 CS 질문을 생성합니다.
     * 이 API는 스케줄러와 동일한 AI 질문 생성 로직을 호출합니다.
     *
     * 경고: 이 API는 개발/테스트 목적으로만 사용해야 합니다.
     *      운영 환경에 배포 시에는 반드시 적절한 인증/인가(예: @PreAuthorize("hasRole('ADMIN')"))를 추가해야 합니다.
     *
     * @return 생성 성공 여부를 담은 응답
     */
    @PostMapping("/generate-manual")
    public ResponseEntity<CustomResponse<Void>> generateManualAiQuestion() {
        aiQuestionCreationService.createAiQuestion();
        return ResponseEntity.ok(CustomResponse.ok(null));
    }
}